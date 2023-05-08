const fs = require("fs");
const path = require("path");
const readline = require("readline");
const esprima = require("esprima");
const escodegen = require("escodegen");

inputFile = process.argv[2];
outputFile = process.argv[3];
mode = process.argv[4];

outDir = path.dirname(outputFile);
if (!fs.existsSync(outDir)) {
  fs.mkdirSync(outDir);
}

function add_comment(all_codes, outFile, commented_codes) {
  console.log(all_codes.length);
  let errorcount = 0;
  for (let i = 0; i < all_codes.length; i++) {
    console.log(i);
    try {
      d = all_codes[i];
      code = d["code"];
      let comment =
        commented_codes[Math.floor(Math.random() * commented_codes.length)];
      const ast = esprima.parse(code);
      modifiedcode = escodegen.generate(ast);
      let index = modifiedcode.indexOf("{");
      modifiedcode =
        modifiedcode.slice(0, index + 1) +
        "\n" +
        comment +
        "\n" +
        modifiedcode.slice(index + 1);
      d["code"] = modifiedcode;
      // console.log(modifiedcode)
      fs.appendFileSync(outFile, JSON.stringify(d));
      fs.appendFileSync(outFile, "\n");
    } catch (e) {
      errorcount += 1;
      // console.log('error occurred')
      console.log(e.message);
    }
  }
  console.log("finished adding comments");
}

const stream = fs.createReadStream(inputFile);
const reader = readline.createInterface({ input: stream });

const reader2 = readline.createInterface({ input: stream });
all_commented_codes = [];
commented_codes = [];
reader2
  .on("line", (line) => {
    const data = JSON.parse(line);
    all_commented_codes.push(data);
  })
  .on("close", () => {
    for (let i = 0; i < all_commented_codes.length; i++) {
      all_lines = all_commented_codes[i]["code"].split("\n");
      const commented = all_lines.map((x) => "// " + x);
      commented_codes.push(commented.join("\n"));
    }
    console.log("Done reading file for commented.");
  });

all_codes = [];
reader
  .on("line", (line) => {
    const data = JSON.parse(line);
    all_codes.push(data);
  })
  .on("close", () => {
    add_comment(all_codes, outputFile, commented_codes);
    console.log("Done reading file.");
    process.exit(1);
  });
