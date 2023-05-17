# Understanding code semantics in Code Summarization

## Python 
### Scripts 
- <strong>corrupt_variables-python.py</strong> : Script to rename variable and function identifiers
- <strong>change_comments.py</strong> : Script to add commented code to the original code. 
- <strong>add_code_after_return_statement.py</strong> : Script to add dead code to the original code. 

### How to Run
- To run a particular script run the following the command.
<code>python filename.py</code>

## Javascript 
### Scripts 
- <strong>parser.js</strong> : Script to rename variable and function identifiers
- <strong>comment_code.js</strong> : Script to add commented code to the original code. 
- <strong>dead_code_parser.js</strong> : Script to add dead code to the original code. 

### How to Run
- Ensure your system has node and npm/yarn preinstalled.
- Ensure all the required libraries are installed in your system. 
    - <code>npm install</code>
- To run a specific file run the following command.
    - <code>node file.js {input_data_json} {output_data_json}</code>
