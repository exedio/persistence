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

function toggleTable(table)
{
	var bodyElement   = document.getElementById("tableBody"   + table);
	var dropElement   = document.getElementById("tableDrop"   + table);
	var renameElement = document.getElementById("tableRename" + table);
	var switchElement = document.getElementById("tableSwitch" + table);
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

function toggleColumn(table,column)
{
	var modifyElement = document.getElementById("columnModify" + table + "x" + column);
	var dropElement   = document.getElementById("columnDrop"   + table + "x" + column);
	var renameElement = document.getElementById("columnRename" + table + "x" + column);
	var switchElement = document.getElementById("columnSwitch" + table + "x" + column);
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

function toggleConstraint(table,constraint)
{
	var dropElement   = document.getElementById("constraintDrop"   + table + "x" + constraint);
	var switchElement = document.getElementById("constraintSwitch" + table + "x" + constraint);
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
