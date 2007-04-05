function checkAll(checkboxName)
{
	var checkboxes = document.body.getElementsByTagName("input");
	for(j=0; j<checkboxes.length; j++)
	{
		var checkbox = checkboxes[j];
		if(checkbox.type=="checkbox" &&
			checkbox.name==checkboxName)
		{
			checkbox.checked = true;
		}
	}
}

function togTab(table)
{
	var bodyElement   = document.getElementById("tabBdy" + table);
	var dropElement   = document.getElementById("tabDrp" + table);
	var renameElement = document.getElementById("tabRen" + table);
	var switchElement = document.getElementById("tabSwt" + table);
	var switchSrc = switchElement.src;
	var switchPart = switchSrc.lastIndexOf("/") + 1;
	if(switchSrc.substring(switchPart)=="checkfalse.png")
	{
		setDisplay(bodyElement,   "block");
		setDisplay(dropElement,   "inline");
		setDisplay(renameElement, "inline");
		switchElement.src = switchSrc.substring(0, switchPart) + "checktrue.png";
	}
	else
	{
		setDisplay(bodyElement,   "none");
		setDisplay(dropElement,   "none");
		setDisplay(renameElement, "none");
		switchElement.src = switchSrc.substring(0, switchPart) + "checkfalse.png";
	}
}

function togCol(table,column)
{
	var modifyElement = document.getElementById("colMod" + table + "x" + column);
	var dropElement   = document.getElementById("colDrp" + table + "x" + column);
	var renameElement = document.getElementById("colRen" + table + "x" + column);
	var switchElement = document.getElementById("colSwt" + table + "x" + column);
	var switchSrc = switchElement.src;
	var switchPart = switchSrc.lastIndexOf("/") + 1;
	if(switchSrc.substring(switchPart)=="checkfalse.png")
	{
		setDisplay(modifyElement, "inline");
		setDisplay(dropElement,   "inline");
		setDisplay(renameElement, "inline");
		switchElement.src = switchSrc.substring(0, switchPart) + "checktrue.png";
	}
	else
	{
		setDisplay(modifyElement, "none");
		setDisplay(dropElement,   "none");
		setDisplay(renameElement, "none");
		switchElement.src = switchSrc.substring(0, switchPart) + "checkfalse.png";
	}
}

function togCon(table,constraint)
{
	var dropElement   = document.getElementById("conDrp" + table + "x" + constraint);
	var switchElement = document.getElementById("conSwt" + table + "x" + constraint);
	var switchSrc = switchElement.src;
	var switchPart = switchSrc.lastIndexOf("/") + 1;
	if(switchSrc.substring(switchPart)=="checkfalse.png")
	{
		setDisplay(dropElement,   "inline");
		switchElement.src = switchSrc.substring(0, switchPart) + "checktrue.png";
	}
	else
	{
		setDisplay(dropElement,   "none");
		switchElement.src = switchSrc.substring(0, switchPart) + "checkfalse.png";
	}
}

function setDisplay(element, display)
{
	if(element!=null)
		element.style.display = display;
}
