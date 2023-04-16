import ast
import astunparse
import json
import argparse
from tqdm import tqdm
import random
import glob
import os
import re

parser = argparse.ArgumentParser()
parser.add_argument('-i', '--input_dir', type=str, default='../CodeT5/data/summarize/python')
parser.add_argument('-o', '--output_dir', type=str, default='../CodeT5/corrupted-function-variable/summarize/python')
parser.add_argument('--error_rate', type=float, default=1.0)

args = parser.parse_args()
ERROR_RATE = args.error_rate
INPUT_DIR = args.input_dir
OUTPUT_DIR = args.output_dir.format(threshold = ERROR_RATE)

if not os.path.exists(OUTPUT_DIR):
    os.makedirs(OUTPUT_DIR)


class TransformName(ast.NodeTransformer):
    
    def __init__(self):
        super(TransformName).__init__()
        self.old_to_new = {}
        self.var_counter = 0
        self.func_counter = 0
        self.arg_counter = 0
        
    def visit_FunctionDef(self, node):
        self.generic_visit(node)
        old_name = node.name
        if old_name in self.old_to_new:
            newname = self.old_to_new[old_name]
        else:
            self.func_counter += 1
            newname = f'func{self.func_counter}'
            self.old_to_new[old_name] = newname
        return ast.FunctionDef(**{**node.__dict__, 'name': newname})
    
    def visit_ClassDef(self, node):
        return node
    
    def visit_Call(self, node):
        func = node.func
        self.generic_visit(node)
        return ast.Call(**{**node.__dict__, 'func': func})

    def visit_Attribute(self, node):
        return node
    
    def visit_ExceptHandler(self, node):
        t = node.type
        self.generic_visit(node)
        return ast.ExceptHandler(**{**node.__dict__, 'type': t})

    def visit_Name(self, node):
        if node.id in self.old_to_new:
            newname = self.old_to_new[node.id]
        else:
            newname = node.id
            if random.random() < ERROR_RATE:
                self.var_counter += 1
                newname = f'var{self.var_counter}'
            self.old_to_new[node.id] = newname
        
        return ast.Name(**{**node.__dict__, 'id': newname})
    
    
    def visit_arg(self, node):
        oldname = node.arg
        if oldname in self.old_to_new:
            newname = self.old_to_new[oldname]
        else:
            newname = oldname
            if random.random() < ERROR_RATE:
                self.arg_counter += 1
                newname = f'arg{self.arg_counter}'
            self.old_to_new[oldname] = newname
        
        return ast.arg(**{**node.__dict__, 'arg': newname})

all_files = glob.glob(f'{INPUT_DIR}/*.jsonl')

for file in all_files:
    skip_counter = 0
    with open(file) as f:
        data = [json.loads(l) for l in f]
    filename = os.path.split(file)[1]

    print(f'processing {filename}...')
    out = open(os.path.join(OUTPUT_DIR, filename), 'w+')
    for d in tqdm(data):
        code = d['code']
        doc_string = d['docstring']
        code = code.replace(doc_string, '')
        code = re.sub(r"\"\"\"\s*\"\"\"", '', code)
        try:
            tree = ast.parse(f'''{code}''')
            d['code'] = astunparse.unparse(TransformName().visit(tree))
        except Exception as e:
            # print('error occurred', str(e))
            skip_counter += 1
        json.dump(d, out)
        out.write('\n')
    print('skipped lines', skip_counter)
    out.close()


