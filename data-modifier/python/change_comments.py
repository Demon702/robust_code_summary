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

input_path = "/work/pi_adrozdov_umass_edu/alodha_umass_edu/nlp_project/robust_code_summary/CodeT5/data/summarize/python/train.jsonl"

output_path = "/work/pi_adrozdov_umass_edu/alodha_umass_edu/nlp_project/robust_code_summary/CodeT5/commented_code/summarize/python/train.jsonl"

if not os.path.exists(os.path.dirname(output_path)):
    os.makedirs(os.path.dirname(output_path))

with open(input_path, 'r') as f:
    data = [json.loads(line) for line in f.readlines()]

for d in tqdm(data):
    code = d['code']
    doc_string = d['docstring']
    comment_pattern = r'""".+?"""'
    comments = re.findall(comment_pattern, code, re.DOTALL)
    
    for comment in comments:
        random_data = random.choice(data)
        random_code = random_data['code']
        random_comment_pattern = r'""".+?"""'
        random_comments = re.findall(random_comment_pattern, random_code, re.DOTALL)
        
        if random_comments:
            random_comment = random_comments[0]
            code = code.replace(comment, random_comment)
        
    code = code.replace(doc_string, '')
    code = re.sub(r"\"\"\"\s*\"\"\"", '', code)
    
    with open(output_path, 'a') as f:
        json.dump({'code': code, 'docstring': doc_string}, f)
        f.write('\n')

        
print("finished")
