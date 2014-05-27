set +e
shopt -s nocasematch # case insesitive

if[$1 == "ie6"] then
	net rpc shutdown -r -f -I 10.1.3.212 -U zktest%zktest # IE6
fi

if[$1 == "ie7"] then
	net rpc shutdown -r -f -I 10.1.3.213 -U zktest%zktest # IE7
fi

if[$1 == "ie8"] then
net rpc shutdown -r -f -I 10.1.3.206 -U zktest%zktest # IE8
fi

if[$1 == "ie9"] then
net rpc shutdown -r -f -I 10.1.3.206 -U zktest%zktest # IE9
fi

if[$1 == "ie10"] then
net rpc shutdown -r -f -I 10.1.3.206 -U zktest%zktest # IE10
fi

if[$1 == "ie11"] then
net rpc shutdown -r -f -I 10.1.3.168 -U zktest%zktest # IE11 on Win8.1
fi

if[$1 == "firefox"] then
net rpc shutdown -r -f -I 10.1.3.222 -U zktest%zktest # Firefox
fi

if[$1 == "chrome"] then
net rpc shutdown -r -f -I 10.1.3.223 -U zktest%zktest # Chrome
fi

if[$1 == "opera"] then
net rpc shutdown -r -f -I 10.1.3.224 -U zktest%zktest # Opera
fi

##Safari on MAC OS X
if[$1 == "safari"] then
ssh -f root@10.1.3.232 shutdown -r now
fi

set -e