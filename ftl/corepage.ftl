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
                document.getElementById("duration").innerHTML = data.duration;
            }

            ws.onclose = function (event) {

            }
        };

        function sendMessage(message) {
            ws.send(message);
        };

        function refresh() {
            document.forms[0].submit();
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
                <input type="submit" value="Play!">
            </p>
            <hr>
            <table border cellpadding="3">
                <tr>
                    <td>Attempts: <b>${gameReplica.attempts}</b></td>
                    <#if gameReplica.message??>
                        <td<#if gameReplica.message == "Less"> bgcolor="red"</#if>
                           <#if gameReplica.message == "More"> bgcolor="green"</#if>
                        >
                            Goal: <b>${gameReplica.message}</b>
                        </td>
                    </#if>
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