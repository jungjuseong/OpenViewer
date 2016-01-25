/**
 * Flash Audio Player
 * 
 * @author hooriza
 */

var audioPlayer = (function() {
	return $Class(
			{
				_term : 0,
				_uniqid : null,
				$init : function(oOptions) {
					var self = this;
					this._uniqid = 'FAP' + new Date().getTime()
							+ parseInt(Math.random() * 1000000);
					var sFlashCode = this._getFlashCode(oOptions.flashSrc,
							this._uniqid, 20, 20, 'window', '', '#ff0000');
					if (navigator.appVersion.indexOf("MSIE") != -1)
						document
								.write('<tbody style="position:absolute; left:-99999px; top:-99999px;">'
										+ sFlashCode + '</tbody>');
					else
						document
								.write('<div style="position:absolute; left:-99999px; top:-99999px;">'
										+ sFlashCode + '</div>');
					window.flashSoundStart = function() {
						self.fireEvent('start');
					};
					window.flashSoundEnd = function() {
						self.fireEvent('end');
					};
				},
				_getFlash : function() {
					return (navigator.appName.indexOf("Microsoft") != -1 ? window
							: document)[this._uniqid];
				},
				_getFlashCode : function(_swfURL_, _flashID_, _width_,
						_height_, _wmode_, _flashVars_, _bgColor_, _fullscreen_) {
					_wmode_ = _wmode_ || 'transparent';
					_bgColor_ = _bgColor_ || '#ffffff';
					_fullscreen_ = _fullscreen_ || 'false';
					var fc_isIE = (navigator.appVersion.indexOf("MSIE") != -1) ? true
							: false;
					var fc_isWin = (navigator.appVersion.toLowerCase().indexOf(
							"win") != -1) ? true : false;
					var fc_isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true
							: false;
					if (fc_isIE && fc_isWin && !fc_isOpera) {
						_object_ = '<object tabindex="-1" classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="'
								+ _width_
								+ '" height="'
								+ _height_
								+ '" id="'
								+ _flashID_ + '" align="middle">';
						_object_ += '<param name="allowScriptAccess" value="always" />';
						_object_ += '<param name="quality" value="high" />';
						_object_ += '<param name="movie" value="' + _swfURL_
								+ '" />';
						_object_ += '<param name="wmode" value="' + _wmode_
								+ '" />';
						_object_ += '<param name="allowFullScreen" value="'
								+ _fullscreen_ + '">';
						_object_ += '<param name="bgcolor" value="' + _bgColor_
								+ '" />';
						_object_ += '<param name="FlashVars" value="'
								+ _flashVars_ + '">';
						_object_ += '</object>';
					} else {
						_object_ = '<embed tabindex="-1" src="'
								+ _swfURL_
								+ '" quality="high" wmode="'
								+ _wmode_
								+ '" allowFullScreen="'
								+ _fullscreen_
								+ '" FlashVars="'
								+ _flashVars_
								+ '" bgcolor="'
								+ _bgColor_
								+ '" width="'
								+ _width_
								+ '" height="'
								+ _height_
								+ '" name="'
								+ _flashID_
								+ '" align="middle" allowScriptAccess="always" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />';
					}
					return _object_;
				},
				play : function(sUrl, oOptions) {
					oOptions = oOptions || {
						loop : false
					};
					if (sUrl instanceof Array)
						sUrl = sUrl.join('|');
					var bLoop = oOptions.loop ? true : false;
					window.fl = this._getFlash();
					try {
						this._getFlash().setSoundTarget(sUrl, bLoop ? 0 : 1);
						this.setTerm();
					} catch (e) {
					}
				},
				stop : function() {
					try {
						this._getFlash().callSoundStop();
					} catch (e) {
					}
				},
				setTerm : function(nTerm) {
					try {
						if (typeof nTerm != 'undefined')
							this._term = nTerm;
						this._getFlash().flashSetTerm(this._term);
					} catch (e) {
					}
				}
			}).extend(nhn.Component);
})();