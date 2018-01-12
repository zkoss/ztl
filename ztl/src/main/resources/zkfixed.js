 zk.afterLoad('zul', function () {
var _widget = {};
// fixed for B50-2988187.ztl
zk.override(zul.Widget.prototype, _widget, {
	doClick_: function (evt, popupOnly) {
		if (!this.shallIgnoreClick_(evt) && !evt.contextSelected) {
			var params = this._popup ? this._parsePopParams(this._popup, evt) : {},
				popup = this._smartFellow(params.id);
			if (popup) {
				evt.contextSelected = true;
				
				// to avoid a focus in IE, we have to pop up it later. for example, zksandbox/#t5
				var self = this,
					xy = params.x !== undefined ? [params.x, params.y]
							: [evt.pageX, evt.pageY];
				// F70-ZK-2007: When type=toggle, close the popup
				if (params.type && params.type == 'toggle' && popup.isOpen()) {
					popup.close({sendOnOpen: true});
				} else {
					popup.parent = this; // B85-ZK-3606: fake parent
					popup._hasFakeParent = true;
					setTimeout(function () { // F70-ZK-2007: Add the type and button number information
						if (self.desktop)
							popup.open(self, xy, params.position ? params.position : null, {sendOnOpen: true, type: params.type, which: 1});
					}, 50); // fixed for B50-2988187.ztl
				}
				evt.stop({dom: true});
			}
		}
		if (popupOnly !== true)
			this.$supers('doClick_', arguments);
	}
});
});
