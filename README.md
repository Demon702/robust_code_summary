# Understanding code semantics in Code Summarization

## Repository Structure
- ```data-modifier/ ``` : The folder contains all the scripts for performing data-transformations. Within this directory, there are subdirectories named ```java```, ```javascript```, and ```python``` that contain scripts specifically designed to transform codes written in their respective languages.
- ```human-annotations/``` : The folder contains the manual evaluation set of data (200 randomly sampled dataset from the original dataset). The files have been named on the basis of the coding language and the respective annotator. 

## Python 
### Scripts 
- <strong>corrupt_variables-python.py</strong> : Script to rename variable and function identifiers
- <strong>change_comments.py</strong> : Script to add commented code to the original code. 
- <strong>add_code_after_return_statement.py</strong> : Script to add dead code to the original code. 

### How to Run
- Ensure your system has python pre-installed. 
- Install the required libraries by running the given command.
    - <code>pip install -r requirements.txt</code>
- To run a particular script run the following the command.
    - <code>python filename.py -i {input_data_directory} -o {output_data_directory}</code>

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

## Java
### Scripts
- <strong>Main.java</strong> : Script to generate corrupted java code (renamed identifiers) and codes with commented code dataset. 
- <strong>MethodNameModifier.class</strong> : Class for renaming the method name identifier. 
- <strong>VariableNameModifier.class</strong> : Class for renaming the variable name identifier.
- <strong>java.jar</strong> : The jar file for running the Main.java code. 

## How to Run
- Ensure java is preinstalled in your system.
- To perform the data transformation for java codes, run the following command.
    - <code>java -jar src/main/java/org/modifier/java.jar</code>