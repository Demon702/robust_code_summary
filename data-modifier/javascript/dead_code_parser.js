const esprima = require("esprima");
const escodegen = require("escodegen");
const estraverse = require("estraverse");
const fs = require("fs");
const path = require("path");
const readline = require("readline");

inputFile = process.argv[2];
outputFile = process.argv[3];

outDir = path.dirname(outputFile);
if (!fs.existsSync(outDir)) {
  fs.mkdirSync(outDir);
}

var returnDict = new Set();
var blockDict = new Set();

const esprimaOptions = { loc: true };

function visit(ast) {
  const keys = Object.keys(ast);
  if (ast["type"] == "ReturnStatement") {
    returnDict.add(ast["loc"]["end"]);
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

function visitToGetBlock(ast) {
  const keys = Object.keys(ast);
  if (ast["type"] == "FunctionDeclaration") {
    blockDict.add(ast["body"]["loc"]);
  }
  for (let i = 0; i < keys.length; i++) {
    const child = ast["body"];
    if (Array.isArray(child)) {
      for (let j = 0; j < child.length; j++) {
        visitToGetBlock(child[j]);
      }
    } else if (isNode(child)) {
      visitToGetBlock(child);
    }
  }
}

function isNode(node) {
  return typeof node === "object";
}

function get_ast(all_codes, outFile, ignored_code) {
  let errorcount = 0;
  console.log(all_codes.length);
  for (let i = 0; i < all_codes.length; i++) {
    // console.log(i)
    try {
      d = all_codes[i];
      modified_code = d["code"];
      returnDict = new Set();
      const ast = esprima.parse(d["code"], esprimaOptions);
      visit(ast);
      if (returnDict.size == 0) continue;

      for (let element of returnDict) {
        all_lines = modified_code.split("\n");
        let index = element["line"];
        let string = "";
        while(true) {
          code_string = all_codes[Math.floor(Math.random() * all_codes.length)]["code"];
          try {
            blockDict = new Set();
            const newast = esprima.parse(code_string, esprimaOptions);
            visitToGetBlock(newast);
            modified_code_string = "";
            for (let element of blockDict) {
              lines = code_string.split("\n");
              let startindex = element["start"]["line"]
              let endindex = element["end"]["line"]
              modified_code_string = lines.slice(startindex, endindex).join("\n");
              // console.log("something", modified_code_string);
            }
            string = modified_code_string;
            if(modified_code_string != "") break;
          }
          catch(e) {
            console.log("error occurred, try again");
          }
        }
        
        if (all_lines.length > index + 2) {
          console.log("skipping this, return statement is not the last line");
          continue;
        }
        all_lines.splice(index, 0, string);
        modified_code = all_lines.join("\n");
      }
      if (d["code"] === modified_code) {
        continue;
      }
      d["code"] = modified_code;
      
      fs.appendFileSync(outFile, JSON.stringify(d));
      fs.appendFileSync(outFile, "\n");
    } catch (e) {
      errorcount += 1;
      console.log(e.message);
    }
  }
  console.log("finished get_ast");
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
