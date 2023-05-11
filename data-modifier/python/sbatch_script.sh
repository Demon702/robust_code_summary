#!/bin/bash
#
#SBATCH --job-name=add-code-after-return-python
#SBATCH --partition=gypsum-titanx
#SBATCH --gres=gpu:1
#SBATCH -o logs/%j-python-add-code-after-return.out
#SBATCH --exclude=gypsum-gpu043
#SBATCH --mem 50GB
#SBATCH -t 2-00:00:00

module load miniconda
conda activate t5
python add_code_after_return_statement.py