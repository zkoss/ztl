import {ClientFunction, Selector} from 'testcafe';
const isProcessing = ClientFunction(() => (!!zAu && !!zAu.processing()) || !!jq.timers.length);
export async function waitResponse(t) {
	let waitTimes = 0;
	await t.wait(300);
    while (await isProcessing()) {
    	if (waitTimes > 20) {
    		console.log('timeout');
    		break;
    	}
        console.log('waiting..');
        await t.wait(300);
        waitTimes++;
    }
}

export async function convertCoordStrToArr(coordStr, coordY) {
	var coordArr = [0, 0];
	if (typeof coordY != 'undefined') {
		coordArr = [parseInt(coordStr), parseInt(coordY)];
	} else {
		coordArr = coordStr.split(',');
	 	coordArr[0] = parseInt(coordArr[0].trim());
		coordArr[1] = parseInt(coordArr[1].trim());
		if (!coordArr || coordArr.length != 2) {
			console.log('parse coord string failed');
			return null;
		}
	}
	return coordArr;
}

export async function verifyTolerant(t, num1, num2, tolerant) {
	if (Math.abs(num1 - num2) > tolerant) {
		t.expect(false).ok(num1 + ' and ' + num2 + 'is out of the tolerant' + tolerant);
	} else {
		t.expect(true).ok(num1 + ' and ' + num2 + 'is in the tolerant' + tolerant);
	}
	
}