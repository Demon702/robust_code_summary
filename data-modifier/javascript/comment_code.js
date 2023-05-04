const fs = require("fs");
const path = require("path");
const readline = require("readline");
const esprima = require("esprima");
const escodegen = require("escodegen");

inputFile = process.argv[2];
outputFile = process.argv[3];

outDir = path.dirname(outputFile);
if (!fs.existsSync(outDir)) {
  fs.mkdirSync(outDir);
}

train_comment_code = [
  "export default function factorial(number) {\n\
    let result = 1;\n\
    for (let i = 2; i <= number; i += 1) {\n\
      result *= i;\n\
    }\n\
    return result;\n\
  }",
  "export default function pascalTriangle(lineNumber) {\n\
    const currentLine = [1];\n\
    const currentLineSize = lineNumber + 1;\n\
    for (let numIndex = 1; numIndex < currentLineSize; numIndex += 1) {\n\
      currentLine[numIndex] = (currentLine[numIndex - 1] * (lineNumber - numIndex + 1)) / numIndex;\n\
    }\n\
    return currentLine;\n\
  }",
  "export default function radianToDegree(radian) {\n\
    return radian * (180 / Math.PI);\n\
  }",
  "export default function isPalindrome(string) {\n\
    let left = 0;\n\
    let right = string.length - 1;\n\
    while (left < right) {\n\
      if (string[left] !== string[right]) {\n\
        return false;\n\
      }\n\
      left += 1;\n\
      right -= 1;\n\
    }\n\
    return true;\n\
  }",
  "export default function traversal(linkedList, callback) {\n\
    let currentNode = linkedList.head;\n\
    while (currentNode) {\n\
      callback(currentNode.value);\n\
      currentNode = currentNode.next;\n\
    }\n\
  }",
  "export const setPixel = (img, { x, y }, color) => {\n\
    const i = y * img.width + x;\n\
    const cellsPerColor = 4;\n\
    img.data.set(color, i * cellsPerColor);\n\
  };",
  "function onEachRail(rail, currentRail) {\n\
    return currentRail === targetRailIndex\n\
      ? [...rail, letter]\n\
      : rail;\n\
  }",
  "const generateMessageVector = (message) => {\n\
    return mtrx.generate(\n\
      [message.length, 1],\n\
      (cellIndices) => {\n\
        const rowIndex = cellIndices[0];\n\
        return message.codePointAt(rowIndex) % alphabetCodeShift;\n\
      },\n\
    );\n\
  };",
  "function insert(item) {\n\
    const hashValues = this.getHashValues(item);\n\
    hashValues.forEach((val) => this.storage.setValue(val));\n\
  }",
  "export default function isPowerOfTwoBitwise(number) {\n\
    if (number < 1) {\n\
      return false;\n\
    }\n\
    return (number & (number - 1)) === 0;\n\
}",
];

function add_comment(all_codes, outFile, commented_codes) {
  console.log(all_codes.length);
  let errorcount = 0;
  for (let i = 0; i < all_codes.length; i++) {
    console.log(i);
    try {
      d = all_codes[i];
      code = d['code'];
      let comment = commented_codes[Math.floor(Math.random()*commented_codes.length)];
      const ast = esprima.parse(code);
      modifiedcode = escodegen.generate(ast);
      let index = modifiedcode.indexOf("{");
      modifiedcode = modifiedcode.slice(0, index+1) + "\n" + comment + "\n" + modifiedcode.slice(index+1);
      d["code"] = modifiedcode;
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

commented_codes = []
for(let i = 0; i < train_comment_code.length; i++) {
    all_lines = train_comment_code[i].split("\n")
    const commented = all_lines.map(x => "// " + x);
    commented_codes.push(commented.join("\n"))
}

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
