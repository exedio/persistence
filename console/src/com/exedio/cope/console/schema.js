function togSch(switchElement)
{
	var bodyElement  = document.getElementById("schBdy");
	var applyElement = document.getElementById("schApp");
	var applyDryElement = document.getElementById("schAppDry");
	var switchSrc = switchElement.src;
	if(switchSrc.substring(switchSrc.length-8)=="true.png")
	{
		setDisplay(bodyElement,  "none");
		setDisplay(applyElement, "none");
		setDisplay(applyDryElement, "none");
		switchElement.src = switchSrc.substring(0, switchSrc.length-8) + "false.png";
	}
	else
	{
		setDisplay(bodyElement,  "block");
		setDisplay(applyElement, "inline");
		setDisplay(applyDryElement, "inline");
		switchElement.src = switchSrc.substring(0, switchSrc.length-9) + "true.png";
	}
}

function togTab(switchElement,table)
{
	var bodyElement = document.getElementById("tabBdy" + table);
	var dropElement = document.getElementById("tabDrp" + table);
	var switchSrc = switchElement.src;
	if(switchSrc.substring(switchSrc.length-8)=="true.png")
	{
		setDisplay(bodyElement, "none");
		setDisplay(dropElement, "none");
		switchElement.src = switchSrc.substring(0, switchSrc.length-8) + "false.png";
	}
	else
	{
		setDisplay(bodyElement, "block");
		setDisplay(dropElement, "inline");
		switchElement.src = switchSrc.substring(0, switchSrc.length-9) + "true.png";
	}
}

function setDisplay(element, display)
{
	if(element!=null)
		element.style.display = display;
}

function edit(span)
{
	var childs = span.childNodes;
	for(i = 0; i<childs.length; i++)
	{
		var child = childs[i];
		var childName = child.nodeName;
		if(childName=="IMG")
			child.style.display = "none";
		else if(childName=="INPUT")
			child.style.display = "inline";
	}
}
