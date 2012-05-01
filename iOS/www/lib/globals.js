// JavaScript Document
var fsqClientID = '1OYPMZW55HEI5CHOJ0AH4EGJATOF0TQD3Z03PRNAJZIKWTPM';
var fsqRedirect = 'http://tikalk.com';

						
$('#map_page').live("pageshow", function() {
	$('#map_canvas').gmap(
		{ 'center' : new google.maps.LatLng(currentLocation.location.lat, currentLocation.location.lng), 
		  'mapTypeControl' : true
		}
	);
                    
	$('#map_canvas').gmap('refresh');
    
    
	$('#map_canvas').gmap('clear', 'markers');

	var marker = $('#map_canvas').gmap(
		'addMarker',{ id:'m_1', 'position':  new google.maps.LatLng(currentLocation.location.lat, currentLocation.location.lng), 'bounds': true , 'animation' : 	google.maps.Animation.DROP}
	)
	.click(function() {
		$('#map_canvas').gmap('openInfoWindow', { 'content': currentLocation.name }, this);
	});
});