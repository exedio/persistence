function checkAllAnalyze()
{
	var checkboxes = document.all.tags("input");
	for(j=0; j<checkboxes.length; j++)
	{
		var checkbox = checkboxes[j];
		if(checkbox.type=="checkbox" &&
			checkbox.name=="ANALYZE_TABLE")
		{
			checkbox.checked = true;
		}
	}
}
