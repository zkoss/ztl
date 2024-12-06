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
let dataTitle = ''
let dataContent = '';
let isHeadLine = false;
let isOnZTLService = false;
let testFailed = false;
let passedCnt = 0;
let failedCnt = 0;
let totalSeconds = 0;
let totalMinutes = 0;
let totalHours = 0;
rl.on('line', (line) => {
	if (line.trim().length > 0) {
		if (isHeadLine) {
			if (dataTitle === '') // only access the first occurrence of title
				dataTitle = line
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
				const timeString = line.substring(line.indexOf('(') + 1, line.indexOf(')')),
					regex = /(\d+)(h|m|s)/g;
				let match;
				// continuous summing of times (when more than one time occurs)
				while ((match = regex.exec(timeString)) !== null) {
					const num = parseInt(match[1], 10),
						unit = match[2];
					if (unit === 'h')
						totalHours += num;
					else if (unit === 'm')
						totalMinutes += num;
					else if (unit === 's')
						totalSeconds += num;
				}
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

	// Normalized total time
	totalMinutes += Math.floor(totalSeconds / 60);
	totalSeconds = totalSeconds % 60;
	totalHours += Math.floor(totalMinutes / 60);
	totalMinutes = totalMinutes % 60;
	let totalTime = ((totalHours > 0 ? totalHours + 'h ': '') + (totalMinutes > 0 ? totalMinutes + 'm ': '') + (totalSeconds > 0 ? totalSeconds + 's ': '')).trim();
	if (totalTime === '')
		totalTime = '0s';

	console.log('Read entire file.');
	dataHead = dataHead + dataTitle + '" tests="' + (passedCnt + failedCnt) + '" failures="' + failedCnt + '" skipped="0" errors="'
		+ failedCnt + '" time="' + totalTime + '" timestamp="" >\n';
	dataContent = dataContent + '</testsuite>';
	var targetFolder = process.argv[3] || '';
	console.log('Write into folder : ' + targetFolder);
	if (targetFolder.length > 0 && !targetFolder.endsWith('/'))
		targetFolder += '/';
	fs.writeFile(targetFolder + 'res.xml', dataHead + dataContent, function (err) {
		if (err)
			console.log(err);
		else
			console.log('Write operation complete.');
	});
	rl.close();
});