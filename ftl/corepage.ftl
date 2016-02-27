<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Guess the number</title>

    <script type="text/javascript">

        var ws;
        var sessionId = ${sessionId};

        init = function () {
            ws = new WebSocket("ws://localhost:8080/");

            ws.onopen = function (event) {
                sendMessage(JSON.stringify({sessionId : sessionId}));
            }

            ws.onmessage = function (event) {
                var data = JSON.parse(event.data);

                if ("duration" in data) {
                    document.getElementById("duration").innerHTML = data.duration;
                }

                if ("attempts" in data && "message" in data) {
                    document.getElementById("gameReplica.attempts").innerHTML = data.attempts;
                    document.getElementById("gameReplica.message.tableCell").style = "display : table-cell";
                    if (data.message == "Less") {
                        document.getElementById("gameReplica.message.tableCell").style = "background-color : red";
                    }
                    if (data.message == "More") {
                        document.getElementById("gameReplica.message.tableCell").style = "background-color : green";
                    }
                    document.getElementById("gameReplica.message").innerHTML = data.message;
                }

                if ("gameOver" in data && data.gameOver == true) {
                    refresh();
                }
            }

            ws.onclose = function (event) {

            }
        };

        function sendMessage(message) {
            ws.send(message);
        };

        function refresh() {
            document.forms[0].submit();
        };

        function sendTurn() {
            var turnInput = document.getElementsByName("turn")[0];
            if (turnInput.value != ""
                    && +turnInput.value >= turnInput.min
                    && +turnInput.value <= turnInput.max) {
                sendMessage(JSON.stringify({turn : turnInput.value}));
            }
            document.forms[0].reset();
            turnInput.focus();
        }

    </script>
</head>
<body onload="init();<#if refreshable??> setInterval(function(){refresh()}, 1000);</#if>">
    <form action="" method="post">

        <#-- Header -->
        <hr>
			<table border cellpadding="3">
				<tr>
					<td>Session ID: <b>${sessionId}</b></td>
					<#if name??><td>User name: <b>${name}</b></td></#if>
					<#if userId??><td>User ID: <b>${userId}</b></td></#if>
					<#if duration??><td>Duration: <b><span id="duration">${duration}</span></b></td></#if>
				</tr>
			</table>  
        <hr>

        <#-- Request name -->
        <#if requestName??>
            <p>
                Enter your name:
                <input name="userName" required autofocus placeholder="Your name">
                <input type="submit" value="Send">
            </p>
        </#if>

        <#-- Wait for authorization -->
        <#if waitAuthorization??>
            <p><b>Wait for authorization!</b></p>
        </#if>

        <#-- Authorization OK -->
        <#if authorizationOK??>
            <p><b>Authorization OK!</b></p>
            <hr><input type="submit" name="startGame" value="Start game!">
        </#if>

        <#-- Game in progress -->
        <#if gameReplica??>
            <p>
                Enter the number between <b>${gameReplica.lower}</b> and <b>${gameReplica.upper}</b>:
            </p>
            <p>
                <input type="number" name="turn"
                       min="${gameReplica.lower}" max="${gameReplica.upper}" step="1"
                       required autofocus placeholder="${gameReplica.lower} ... ${gameReplica.upper}"
                >
                <input type="button" value="Play!" onclick="sendTurn();">
            </p>
            <hr>
            <table border cellpadding="3">
                <tr>
                    <td>Attempts: <b><span id="gameReplica.attempts">${gameReplica.attempts}</span></b></td>
                    <td id="gameReplica.message.tableCell"
                        <#if gameReplica.message??> style="display : table-cell"
                            <#if gameReplica.message == "Less"> bgcolor="red"</#if>
                            <#if gameReplica.message == "More"> bgcolor="green"</#if>
                        <#else> style="display : none"
                        </#if>
                    >
                        Goal: <b><span id="gameReplica.message"><#if gameReplica.message??>${gameReplica.message}</#if></span></b>
                    </td>
                </tr>
            </table>
        </#if>

        <#-- Game over -->
        <#if gameOver??>
            <p>
                <b>Game over!</b>
            </p>
            <p>
                You found <b>${gameOver.goal}</b> with <b>${gameOver.attempts}</b> attempts.
            </p>
            <hr>
            <table border cellpadding="3">
                <caption><b>High Scores:</b></caption>
                <tr><th>#</th><th>Name</th><th>Attempts</th><th>Time</th></tr>
                <#list gameOver.highScores as score>
                    <tr<#if name == score.name
                        && gameOver.attempts == score.attempts
                        && duration == score.duration> bgcolor="yellow"</#if>>
                        <td align="center">${score?counter}</td>
                        <td>${score.name}</td>
                        <td align="center">${score.attempts}</td>
                        <td align="center">${score.duration}</td>
                    </tr>
                </#list>
            </table>
            <hr><input type="submit" name="startGame" value="Play again!">
        </#if>

        <#-- Session ID holder -->
        <input type="hidden" name="sessionId" value="${sessionId}">
    </form>
</body>
</html>