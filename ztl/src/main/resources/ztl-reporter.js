const readline = require('readline');
const fs = require('fs');

const rl = readline.createInterface({
  input: fs.createReadStream(process.argv[2]),
  crlfDelay: Infinity
});

let testStartCnt = 0;
let testClass = '';
let testName = '';
let dataHead = '<?xml version="1.0" encoding="UTF-8" ?>\n<testsuite name="TestCafe Tests: ';
let dataContent = '';
let isHeadLine = false;
let isOnZTLService = false;
let testFailed = false;
let passedCnt = 0;
let failedCnt = 0;
let totalTime = 0;
rl.on('line', (line) => {
	if (line.trim().length > 0) {
		if (isHeadLine) {
			dataHead = dataHead + line;
			isHeadLine = false;
		} else if (isOnZTLService) { //skip zscript
			if (line.indexOf('await ztl.waitResponse(t);') > -1) {
				isOnZTLService = false;
				dataContent = dataContent + '[ onZTLService Skipped ]\n';
			}
		} else {
			if (line.indexOf('Running tests in:') > -1)
				isHeadLine = true;
			else if (line.indexOf("await ClientFunction(() => {zAu.send(new zk.Event") > -1) {
				isOnZTLService = true;
			} else if (line.indexOf('ZTL TEST -') > -1) {
				if (testStartCnt > 0) {
					//end
					if (testFailed)
						dataContent = dataContent + ']]></failure>\n';
					dataContent = dataContent + '</testcase>\n';
					testStartCnt = 0;
					testFailed = false;
				}
				testStartCnt++;
				testName = line.substring(line.indexOf('('));
				testClass = line.replace('ZTL TEST -', '').replace(testName, '').trim();
				dataContent = dataContent + '<testcase classname="' + testClass + '" name="test' + testName + '" time="0.99">\n';
			} else if (line.indexOf('✓') > -1 || line.indexOf('√') > -1) { // passed start
				dataContent = dataContent + '</testcase>\n';
				testStartCnt = 0;
				passedCnt++;
			} else if (line.indexOf('✖') > -1 || line.indexOf('×') > -1) { // failed start
				testFailed = true;
				failedCnt++;
				dataContent = dataContent + '<failure> <![CDATA[\n';
			} else if (testFailed) {
				dataContent = dataContent + line + '\n';
			} else if (line.indexOf('passed (') > -1 || line.indexOf('failed (') > -1) {
				totalTime = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
			}
		}
	}
//	console.log(`Line from file: ${line}`);
}).on('close', () => {
	if (testFailed) {
		dataContent = dataContent + ']]></failure>\n';
	}
	if (testStartCnt > 0) {
		dataContent = dataContent + '</testcase>\n';
	}
	console.log('Read entire file.');
	dataHead = dataHead + '" tests="' + (passedCnt + failedCnt) + '" failures="' + failedCnt + '" skipped="0" errors="'
		+ failedCnt + '" time="' + totalTime + '" timestamp="" >\n';
	dataContent = dataContent + '</testsuite>';
	fs.writeFile('res.xml', dataHead + dataContent, function (err) {
		if (err)
			console.log(err);
		else
			console.log('Write operation complete.');
	});
	rl.close();
});