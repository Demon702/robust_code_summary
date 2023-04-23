const esprima = require("esprima");
const escodegen = require("escodegen");
const estraverse = require("estraverse");
const fs = require("fs");
const path = require('path');
const readline = require("readline");

inputFile = process.argv[2]
outputFile = process.argv[3]

outDir = path.dirname(outputFile)
if (!fs.existsSync(outDir)) {
  fs.mkdirSync(outDir);
}

var functionDict = {};
var funcCount = 0;
var varCount = 0;
var variableDict = {};

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

function get_ast(all_codes, outFile) {
  const esprimaOptions = { loc: true };
  let errorcount = 0;
  console.log(all_codes.length)
  for (let i = 0; i < all_codes.length; i++) {
    console.log(i)
    try {
      d = all_codes[i]
      functionDict = {};
      funcCount = 0;
      varCount = 0;
      variableDict = {};
      const ast = esprima.parse(d['code'], esprimaOptions);
      visit(ast);
      modifiedcode = rename(ast, functionDict);
      modifiedcode = rename(ast, variableDict);
      d['code'] = modifiedcode
      fs.appendFileSync(outFile, JSON.stringify(d));
      fs.appendFileSync(outFile, "\n");
    } catch (e) {
      errorcount += 1;
      // console.log('error occurred')
      console.log(e.message);
    }
  }
  console.log('finished get_ast');
}

const stream = fs.createReadStream(inputFile);
const reader = readline.createInterface({ input: stream });

all_codes = [];
reader
  .on("line", (line) => {
    const data = JSON.parse(line);
    all_codes.push(data);
  })
  .on("close", () => {
    get_ast(all_codes, outputFile);
    console.log("Done reading file.");
    process.exit(1);
  });


// let listOfFunctions = [
//   async () =>
//     new Promise((resolve) => {
//       read_files();
//       resolve(true);
//     }),
// ];

// let executeListOfFunctions = async (listOfFunctions) => {
//   return Promise.all(listOfFunctions.map((func) => func()));
// };

// // calling main function
// executeListOfFunctions(listOfFunctions);

// // const ast = acorn.parse(code, { ecmaVersion: 2021 });

// // console.log(ast);
