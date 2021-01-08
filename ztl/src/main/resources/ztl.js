import {ClientFunction} from 'testcafe';

const colorString = require('color-string');
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

export async function convertCoordStrToArr(coordStr, isDragFrom) {
	var coordArr = coordStr.split(',');
	coordArr[0] = parseInt(coordArr[0].trim());
	coordArr[1] = parseInt(coordArr[1].trim());
	if (!coordArr || coordArr.length != 2) {
		console.log('parse coord string failed');
		return null;
	}
	//modify
	if (isDragFrom) {
		if (coordArr[0] > 0)
			coordArr[0] = coordArr[0] - 1;
		if (coordArr[1] > 0)
			coordArr[1] = coordArr[1] - 1;
	}
	return coordArr;
}

export async function verifyTolerant(t, num1, num2, tolerant) {
	if (Math.abs(parseInt(num1) - parseInt(num2)) > parseInt(tolerant)) {
		await t.expect(false).ok(num1 + ' and ' + num2 + ' is out of the tolerant ' + tolerant);
	} else {
		await t.expect(true).ok(num1 + ' and ' + num2 + ' is in the tolerant ' + tolerant);
	}

}

export async function doScroll(config) {
	config.locator = await config.locator();
	await ClientFunction(() => {
		var target = config.locator,
			isVert = config.scrollType == 'vertical',
			percent = config.percent,
			dist = config.dist,
			wgt = zk.Widget.$(jq(target)),
			body = wgt.$n('body'),
			cave = wgt.$n('cave');
		if (!config.noBody) {
			if (percent) {
				var bpad = wgt.$n('bpad'),
					tpad = wgt.$n('tpad'),
					totalDist;
				if (isVert) {
					totalDist = jq(cave).height() - (body ? jq(body).height() : 0);
					if (bpad && tpad) {
						totalDist += bpad.offsetHeight + tpad.offsetHeight;
					}
				} else {
					totalDist = jq(cave).width() - jq(body).width();
				}
				dist = Math.round(totalDist * percent / 100);
			}
		} else {
			var caveChildren = jq(cave).children(),
				childrenDist = 0;
			if (isVert) {
				for (var i = 0; i < caveChildren.length; i++)
					childrenDist += caveChildren.eq(i).height();
				dist = Math.round((childrenDist - jq(wgt).height()) * percent / 100);
			} else {
				for (var i = 0; i < caveChildren.length; i++)
					childrenDist += caveChildren.eq(i).width();
				dist = Math.round((childrenDist - jq(wgt).width()) * percent / 100);
			}
			body = wgt.$n();
		}
		var nScrollBar = wgt._scrollbar;
		if (isVert) {
			if (nScrollBar)
				wgt._scrollbar.scrollTo(0, dist);
			else {
				jq(body ? body : (cave ? cave : wgt)).scrollTop(Math.abs(dist));
			}
		} else {
			if (nScrollBar)
				wgt._scrollbar.scrollTo(dist, 0);
			else
				jq(body ? body : (cave ? cave : wgt)).scrollLeft(Math.abs(dist));
		}
	}, {dependencies: {config}})();
}

export async function isEqualColor(color1, color2) {
	return colorString.get.rgb(color1).toString() == colorString.get.rgb(color2).toString();
}

export async function getScrollTop(config) {
	config.locator = await config.locator();
	return await ClientFunction(() => {
		var wgt = zk.Widget.$(jq('#' + config.locator.id)),
			nScrollBar = wgt._scrollbar;
		if (nScrollBar) {
			var str = jq(wgt).find(".z-scrollbar")[0].style.top;
			return parseInt(str);
		} else {
			return jq(wgt.$n("body")).scrollTop();
		}
	}, {dependencies: {config}})();
}

export async function getScrollLeft(config) {
	config.locator = await config.locator();
	return await ClientFunction(() => {
		var wgt = zk.Widget.$(jq('#' + config.locator.id)),
			nScrollBar = wgt._scrollbar;
		if (nScrollBar) {
			var str = jq(wgt).find(".z-scrollbar")[0].style.left;
			return parseInt(str);
		} else {
			return jq(wgt.$n("body")).scrollLeft();
		}
	}, {dependencies: {config}})();
}

export async function hasHScrollbar(config) {
	config.locator = await config.locator();
	return await ClientFunction(() => {
		var locator = jq('#' + config.locator.id),
			wgt = zk.Widget.$(locator),
			nonNativeScrollBar = wgt._scrollbar;
		if (nonNativeScrollBar) {
			return !!jq(wgt).find('.z-scrollbar-horizontal')[0];
		} else {
		 	var borderHeight = Math.round(parseInt(locator.css('borderTopWidth'))) + Math.round(parseInt(locator.css('borderBottomWidth'))),
		 		scrollbarHeight = locator[0].offsetHeight - borderHeight - locator[0].clientHeight;
			return scrollbarHeight > 0;
		}
	}, {dependencies: {config}})();
}

export async function hasVScrollbar(config) {
	config.locator = await config.locator();
	return await ClientFunction(() => {
		var locator = jq('#' + config.locator.id),
			wgt = zk.Widget.$(locator),
			nonNativeScrollBar = wgt._scrollbar;
		if (nonNativeScrollBar) {
			return !!jq(wgt).find('.z-scrollbar-vertical')[0];
		} else {
			var borderHeight = Math.round(parseInt(locator.css('borderLeftWidth'))) + Math.round(parseInt(locator.css('borderRightWidth'))),
				scrollbarHeight = locator[0].offsetHeight - borderHeight - locator[0].clientHeight;
			return scrollbarHeight > 0;
		}
	}, {dependencies: {config}})();
}

export async function splitInputText(text) {
	var result = '',
		textArr = text.split('');
	for (var i = 0, l = textArr.length; i < l; i++) {
		if (' ' == textArr[i]) {
			result += ' space';
		} else {
			result += ' ' + textArr[i];
		}
	}
	return result.trim();
}

export async function isBrowserIgnored(ignoreBrowsers) {
	return await ClientFunction(() => {
		var browserArr = ignoreBrowsers.split(',');
		for (var i = 0, l = browserArr.length; i < l; i++) {
			var browser = browserArr[i];
			var ignored = zk[browser] || ('ie11' == browser && zk.ie11_); // chrome -> zk.chrome
			if (ignored)
				return true;
		}
		return false;
	}, {dependencies: {ignoreBrowsers}})();
}