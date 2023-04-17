const esprima = require("esprima");
const escodegen = require("escodegen");
const estraverse = require("estraverse");
const fs = require("fs");
const readline = require("readline");

const filepath = "../data/data/summarize/javascript/test.jsonl";

const stream = fs.createReadStream(filepath);
const reader = readline.createInterface({ input: stream });

let functionDict = {};
let funcCount = 0, varCount = 0;
let variableDict = {};

function visit(ast) {
  const keys = Object.keys(ast);
  if (ast["type"] == "FunctionDeclaration") {
    if (ast.id.type == "Identifier" && !(ast.id.name in functionDict)) {
      functionDict[ast.id.name] = "func" + funcCount;
      funcCount += 1;
    }

    if (ast.params) {
      ast.params.forEach((param) => {
        if (param.type == "Identifier" && !(param.name in variableDict)) {
          variableDict[param.name] = "variable" + varCount;
          varCount += 1;
        }
      });
    }
  }
  if (ast["type"] == "VariableDeclaration") {
    ast.declarations.forEach((declarator) => {
      if (
        declarator.id.type == "Identifier" &&
        !(declarator.id.name in variableDict)
      ) {
        variableDict[declarator.id.name] = "variable" + varCount;
        varCount += 1;
      }
    });
  }
  for (let i = 0; i < keys.length; i++) {
    const child = ast["body"];
    if (Array.isArray(child)) {
      for (let j = 0; j < child.length; j++) {
        visit(child[j]);
      }
    } else if (isNode(child)) {
      visit(child);
    }
  }
}

function isNode(node) {
  return typeof node === "object";
}

function rename(ast, renamingObj) {
  function callback(node) {
    if (node.type === "Identifier") {
      if (node.name in renamingObj) {
        node.name = renamingObj[node.name];
      }
    }
  }
  estraverse.traverse(ast, { enter: callback });
  return escodegen.generate(ast);
}

function get_ast(all_codes) {
  const esprimaOptions = { loc: true };
  let errorcount = 0;
  
  for (let i = 0; i < all_codes.length; i++) {
    try {
        const ast = esprima.parse(all_codes[i], esprimaOptions);
        visit(ast);
        modifiedcode = rename(ast, functionDict);
        modifiedcode = rename(ast, variableDict);
        fs.appendFileSync('file_test.jsonl', "\n");
        fs.appendFileSync('file_test.jsonl', JSON.stringify(modifiedcode));
        functionDict = {}, funcCount = 0, varCount = 0, variableDict = {};
      } catch (e) {
        errorcount += 1;
        console.log(e.message);
        // console.log(all_codes[i]);
      }
  }
}

function read_files() {
  let all_codes = [];
  try {
    reader
      .on("line", (line) => {
        const data = JSON.parse(line);
        code = data["original_string"];
        all_codes.push(code);
      })
      .on("close", () => {
        get_ast(all_codes);
        console.log("Done reading file.");
      });
  } catch (error) {
    console.log(error);
    console.log("Error occurred !!");
  }
}

let listOfFunctions = [
  async () =>
    new Promise((resolve) => {
      read_files();
      resolve(true);
    }),
];

let executeListOfFunctions = async (listOfFunctions) => {
  return Promise.all(listOfFunctions.map((func) => func()));
};

// calling main function
executeListOfFunctions(listOfFunctions);

// const ast = acorn.parse(code, { ecmaVersion: 2021 });

// console.log(ast);
