import libcst as cst
import json
from libcst import RemovalSentinel, Comment
import libcst.matchers as m
from tqdm import tqdm
import argparse
import random
import glob
import os

random.seed(0)
parser = argparse.ArgumentParser()
parser.add_argument('-i', '--input_dir', type=str, default='../../CodeT5/data/summarize/python')
parser.add_argument('-o', '--output_dir', type=str, default='../../CodeT5/code-after-return/summarize/python')
parser.add_argument('--corruption_rate', type=float, default=0.5)

args = parser.parse_args()
CORRUPTION_RATE = args.corruption_rate
INPUT_DIR = args.input_dir
OUTPUT_DIR = args.output_dir.format(threshold = CORRUPTION_RATE)

if not os.path.exists(OUTPUT_DIR):
    os.makedirs(OUTPUT_DIR)

class RemoveComments(cst.CSTTransformer):
    def leave_Comment(
                self, original_node: "Comment", updated_node: "Comment"
        ) -> "Comment":
            return RemovalSentinel(RemovalSentinel.REMOVE)
    
    def leave_Expr(self, original_node, updated_node):
        if m.matches(updated_node.value, m.SimpleString()):
            return RemovalSentinel(RemovalSentinel.REMOVE)
        return updated_node
    
def get_func_body(code):
    tree = cst.parse_module(f'''{code}''')
    new_tree = tree.visit(RemoveComments())
    func_body = new_tree.body[0].body.body
    return func_body

class AddCodeAfterReturn(cst.CSTTransformer):
    def __init__(self, code_to_add):
        self.return_exists_array = []
        self.code_to_add = get_func_body(code_to_add)
        
    def visit_FunctionDef(self, node):
        self.return_exists_array.append(False)
        return True

    def leave_Return(self, original_node, updated_node):
        self.return_exists_array.pop()
        self.return_exists_array.append(True)
        return updated_node

    def leave_FunctionDef(self, original_node, updated_node):
        return_exists = self.return_exists_array.pop()
        if return_exists:
            updated_node = updated_node.with_changes(
                body = updated_node.body.with_changes(body=tuple(list(updated_node.body.body) + list(self.code_to_add)))
            )

        return updated_node
    
all_files = glob.glob(f'{INPUT_DIR}/*.jsonl')
print(all_files)
for file in all_files:
    skip_counter = 0
    with open(file) as f:
        data = [json.loads(l) for l in f]
    filename = os.path.split(file)[1]
    file_len = len(data)
    print(f'processing {filename}...')
    out = open(os.path.join(OUTPUT_DIR, filename), 'w+')
    for d in tqdm(data):
        code = d['code']
        if random.random() < CORRUPTION_RATE:
            try:
                code_to_add = data[random.randint(0, file_len)]['code']
                tree = cst.parse_module(f'''{code}''')
                transformer = AddCodeAfterReturn(code_to_add)
                new_tree = tree.visit(RemoveComments()).visit(transformer)
                del d['code_tokens']
                d['code'] = new_tree.code
            except Exception as e:
                skip_counter += 1

        json.dump(d, out)
        out.write('\n')
    out.close()
    print(f'No of skipped files: {skip_counter}')