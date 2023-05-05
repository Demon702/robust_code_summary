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

ignored_code = [
  'for (let i = 0; i < cars.length; i++) { text += cars[i] + "<br>"; }',
  "numbers.sort((a, b) => a - b);",
  "const data = await response.json();",
  "const maxi = (num1 > num2) ? num1 : num2;",
  "var data = fruits.find(element => element.length > 6);",
];

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

function isNode(node) {
  return typeof node === "object";
}

function get_ast(all_codes, outFile) {
  const esprimaOptions = { loc: true };
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
        let str = ignored_code[Math.floor(Math.random() * ignored_code.length)];
        if (all_lines.length > index + 2) {
          console.log("skipping this, return statement is not the last line");
          continue;
        }
        // console.log(index, all_lines.length)
        all_lines.splice(index, 0, str);
        modified_code = all_lines.join("\n");
      }
      if (d["code"] === modified_code) {
        continue;
      }
      d["code"] = modified_code;
    //   console.log(modified_code);
    //   console.log("\n\n");
      fs.appendFileSync(outFile, JSON.stringify(d));
      fs.appendFileSync(outFile, "\n");
    } catch (e) {
      errorcount += 1;
      // console.log('error occurred')
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
