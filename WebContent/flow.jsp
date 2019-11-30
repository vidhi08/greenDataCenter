<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Data Center</title>
		<script src="jquery-3.4.1.min.js"></script>
		<style>
			.request_time {
				background: ghostwhite;
			    height: 30px;
			    vertical-align: middle;
			    line-height: 30px;
			    text-align: center;
    			font-weight: bold;
    			border-style: ridge;
			}
			.server_threshold {
				background: ghostwhite;
			    height: 30px;
			    vertical-align: middle;
			    line-height: 30px;
			    text-align: center;
    			font-weight: bold;
    			border-bottom-style: ridge;
			    border-left-style: ridge;
			    border-right-style: ridge;
			}
			.request {
				background: ghostwhite;
			    height: 30px;
			    vertical-align: middle;
			    line-height: 30px;
			    text-align: center;
    			font-weight: bold;
    			border-left-style: ridge;
			    border-right-style: ridge;
			}
			.Num_servers {
				background: ghostwhite;
			    height: 30px;
			    vertical-align: middle;
			    line-height: 30px;
			    text-align: center;
    			font-weight: bold;
    			border-style: ridge;
			}
			.server {
				width: 130px;
			    height: 100px;
			    text-align: center;
			    vertical-align: middle;
			    line-height: 100px;
    			display: none;
    			margin: 13px;
    			opacity: 0;
			}
			.main_holder {
				display: inline-block;
				position: relative;
			    border-style: dotted;
			    border-color: aquamarine;
			    width: 800px;
			    height: fit-content;
			    padding: 15px;
			    background: black;
			}
			.server_holder {
				position: relative;
			    width: 100%;
			    display: block;
			}
			.server_name {
				width: 100%;
			    background: black;
			    height: 33px;
			    color: wheat;
			    text-align: center;
			    vertical-align: middle;
			    line-height: 33px;
			    border-style: double;
			}
			.server_blocks {
				width: 100%;
			    background: grey;
			    height: 33px;
			    border-color: wheat;
			    border-style: double;
			}
			.Initial_servers {
				background: ghostwhite;
			    height: 30px;
			    vertical-align: middle;
			    line-height: 30px;
			    text-align: center;
			    font-weight: bold;
			    border-style: ridge;
			    border-top-style: none;
			}
			#log_holder {
				width: 200px;
			    position: relative;
			    display: inline-block;
			    border-style: groove;
			    padding: 10px;
			}
		</style>
	</head>
	<body>
		<div style = "display: block;overflow: hidden;width: 1600px;position: absolute;">
			<div class = "main_holder">
				<div class = "request_time">Time of Response: ${responsetime} milliseconds</div>
				<div class = "server_threshold">Maximum Threshold of Servers: ${max_threshold_perserver}</div>
				<div class = "request">Request Received: ${request_count}</div>
				<div class = "Num_servers">Number of Servers Running: ${server_count}</div>
				<div class = "Initial_servers">Initial Server On: 2</div>
				<div class = "server_holder">
					<c:forEach var="serverName" items="${serverMap}" varStatus="loop">
						<div class="server" id="${loop.index}"> 
					  	<div style="width: 100px;height: 118px;float: left;margin-right: 10px;">
						  	  <div class="server_blocks"></div>
							  <div class="server_name"><c:out value="${serverName}"/></div>
							  <div class="server_blocks"></div>
					  	</div>
					  	<div style="width: 20px;height: 118px;float: left;">
					  		<div id = "green_${loop.index}" style="width: 100%;height: 0px;background: lawngreen;margin-top: 10px; opacity: 0"></div>
					  		<div id = "yellow_${loop.index}" style="width: 100%;height: 0px;background: yellow;margin-top: 20px; opacity: 0"></div>
					  		<div id = "red_${loop.index}" style="width: 100%;height: 0px;background: red;margin-top: 20px; opacity: 0"></div>
					  	</div>
					  </div>
					</c:forEach>
				</div>
			</div>
			<div id="log_holder">
				Project is running</br>
				${request_count} jobs on the stack</br>
				server time ${start_time}</br>
				new Server added</br>
				<c:forEach var="serverName" items="${serverMap}" varStatus="loop">
					<div> 
				  		Server Allocated <c:out value="${serverName}"/></br>
				  	</div>
				</c:forEach>
				Stack empty</br>
				Current Temperature: ${temperature} degree</br>
				Time taken: ${responsetime} milliseconds</br>
			</div>
		</div>
		<script>
			$('document').ready(function(){
				var responseTime = ${responsetime};
				var list = "<c:forEach var='serverName' items='${serverMap}' varStatus='loop'><c:out value='${serverName}'/></br></c:forEach>" 
				var serverNameList = list.split('</br>');
				var colorArr = ["green", "yellow", "red"];
				repeatAnimation(0, 0);
				function repeatAnimation(index, color){
					$("#"+index).css({display: 'inline-block', opacity : 0});
					$("#"+index).animate({
					    opacity: 1,
					  }, responseTime, function() {
						  colorAnimation(index, color);
					  });
				}
				function colorAnimation(index, color){
					$("#"+colorArr[color]+"_"+index).css({opacity : 0});
					$("#"+colorArr[color]+"_"+index).animate({
					    opacity: 1,
					    height: '20px'
					  }, 1100, function() {
						  color++;
						  if (color < colorArr.length){
							colorAnimation(index, color)
						  } else {
							  color = 0;
							  index++;
							  repeatAnimation(index, color);
						  }
					  });
				}
			});
		</script>
	</body>
</html>