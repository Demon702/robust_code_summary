import ast
import astunparse
import json
import argparse
from tqdm import tqdm
import random
import glob
import os
import re
import ipdb

parser = argparse.ArgumentParser()
parser.add_argument('-i', '--input_path', type=str, default='../../CodeT5/data/summarize/python/valid.jsonl')
parser.add_argument('-o', '--output_path', type=str, default='../../CodeT5/commented_code/summarize/python/valid.jsonl')

args = parser.parse_args()
INPUT_PATH = args.input_path
OUTPUT_PATH = args.output_path

if not os.path.exists(os.path.dirname(OUTPUT_PATH)):
    os.makedirs(os.path.dirname(OUTPUT_PATH))

with open(INPUT_PATH, 'r') as f:
    data = [json.loads(line) for line in f.readlines()]

f = open(OUTPUT_PATH, 'w+')

for d in tqdm(data):
    
    code = d['code']
    doc_string = d['docstring']
    
    comment_pattern = r'""".+?"""'
    comments = re.findall(comment_pattern, code, re.DOTALL)
    
    random_data = random.choice(data)
    random_code = random_data['code']
    code_lines = random_code.split('\n')
    commented_lines = ['#' + line for line in code_lines]
    commented_code = '\n'.join(commented_lines)
    

    if comments:
        comment = comments[0]
        code = code.replace(comment, commented_code)
        
    code = code.replace(doc_string, '')
    code = re.sub(r"\"\"\"\s*\"\"\"", '', code)
    
    # ipdb.set_trace()
        
    del d['code_tokens']
    
    d['code'] = code
    json.dump(d, f)
    f.write("\n")
    
f.close()
    
