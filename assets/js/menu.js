/*	Sort each row of our accordion menu based on the 'order' attribute in the 
 *	YAML front matter
 */
$(document).ready(function() {
	$('table').each(function() {
		var table = $(this);

		$(this).find('tr').sort(function(a, b) {
			if(Number($(a).attr('order')) < Number($(b).attr('order'))) {
				return -1;
			}
			else {
				return 1;
			}
		}).each(function() {
			$(table).append($(this));
		});
	});
});

/*----------------------------------------------------------------------------*/

/*	Set a cookie when a panel in the accordion is expanded. Remove the cookie
*	when the panel is collapsed. When loading a new page check the cookies to 
*	to properly display the panel in the expanded/collapsed state. 
*/
$(document).ready(function () {
	$('#accordion').on('shown.bs.collapse', function() {
		$('.panel-collapse.collapse').each(function() {
			if($(this).attr('aria-expanded') === 'true') {
				if(Cookies.get($(this).attr('id')) == null ||
					Cookies.get($(this).attr('id') === 'false')) {
					Cookies.set($(this).attr('id'), 'active');
				}
			}
		});
	});

	$("#accordion").on('hidden.bs.collapse', function() {
		$('.panel-collapse.collapse').each(function() {
			if($(this).attr('aria-expanded') === 'false') {
				Cookies.remove($(this).attr('id'));
			}
		});
	});

	$('.panel-collapse.collapse').each(function() {
		if(Cookies.get($(this).attr('id')) === 'active') {
			$(this).addClass('in');
		}
	});
});
