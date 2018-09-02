import {ClientFunction, Selector} from 'testcafe';
const isProcessing = ClientFunction(() => !!zAu.processing() || !!jq.timers.length);
export async function waitResponse(t) {
	await t.wait(300);
    while (await isProcessing()) {
        console.log('waiting..');
        await t.wait(300);
    }
}

export async function convertCoordStrToArr(coordStr, coordY) {
	var coordArr = [0, 0];
	if (coordY) {
		coordArr = [parseInt(coordStr), parseInt(coordY)];
	} else {
		coordArr = coordStr.split(',');
	} 
	if (!coordArr || coordArr.length != 2) {
		console.log('parse coord string failed');
		return null;
	}
	coordArr[0] = coordArr[0].trim();
	coordArr[1] = coordArr[1].trim();
	return coordArr;
}

export async function verifyTolerant(t, num1, num2, tolerant) {
	if (Math.abs(num1 - num2) > tolerant) {
		t.expect(false).ok(num1 + ' and ' + num2 + 'is out of the tolerant' + tolerant);
	} else {
		t.expect(true).ok(num1 + ' and ' + num2 + 'is in the tolerant' + tolerant);
	}
	
}