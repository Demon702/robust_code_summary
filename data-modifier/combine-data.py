import json
import sys
import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument('-c', '--clean', type=str, default='../../CodeT5/data/summarize/python/train.jsonl')
parser.add_argument('-co', '--corrupted', type=str, default='../../CodeT5/corrupted-function-variable/summarize/python/train.jsonl')
parser.add_argument('-o', '--output', type=str, default='../../CodeT5/combined_data/summarize/python/train.jsonl')

args = parser.parse_args()
clean = args.clean
corrupted = args.corrupted
output = args.output
out_dir = os.path.dirname(output)

def process_corrupted_data(d):
    if 'code_tokens' in d:
        del d['code_tokens']
    return d

if not os.path.exists(out_dir):
    os.makedirs(out_dir)

with open(clean, 'r') as f:
    data1 = [json.loads(l) for l in f]

with open(corrupted, 'r') as f:
    data2 = [process_corrupted_data(json.loads(l)) for l in f]

final_data = data1 + data2
out = open(output, 'w+')

for d in final_data:
    json.dump(d, out)
    out.write('\n')

