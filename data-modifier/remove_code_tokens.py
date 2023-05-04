import json
import sys
import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument('-i', '--input', type=str, default='../../CodeT5/corrupted-function-variable/summarize/python/train.jsonl')
parser.add_argument('-o', '--output', type=str, default='{input}')

args = parser.parse_args()
input = args.input
output = args.output.format(input=input)
out_dir = os.path.dirname(output)

def process_corrupted_data(d):
    del d['code_tokens']
    return d

with open(input, 'r') as f:
    data = [process_corrupted_data(json.loads(l)) for l in f]

with open(output, 'w+') as out:
    for d in data:
        json.dump(d, out)
        out.write('\n')

