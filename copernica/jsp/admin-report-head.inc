
		<style type="text/css">
			span.ok     { background-color:#ccffcc; }
			span.yellow { background-color:#ffff33; }
			span.red    { background-color:#ff3333; }
			li.ok     { background-color:#ccffcc; }
			li.yellow { background-color:#ffff33; }
			li.red    { background-color:#ff3333; }
			ul.ok     { background-color:#ccffcc; }
			ul.yellow { background-color:#ffff33; }
			ul.red    { background-color:#ff3333; }
		</style>
		<script type="text/javascript">
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
		</script>