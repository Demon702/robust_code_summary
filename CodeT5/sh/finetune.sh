#!/bin/bash
#
#SBATCH --job-name=codet5-python
#SBATCH --partition=gypsum-titanx
#SBATCH --gres=gpu:1
#SBATCH -o logs/%j-python-finetune.out
#SBATCH --exclude=gypsum-gpu043
#SBATCH --mem 50GB


module load miniconda
conda activate t5
python run_exp.py --model_tag codet5_small --task summarize --sub_task python