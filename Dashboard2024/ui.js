// const { Console } = require("console");
// import console from "console"

// initial camera settings
var driveReversed = false
var allKeysPressed = new Array()
var angle = 0
var selectedPath = ""

var single_substation_coords = []

var pathNames = ["Mid321", "Mid3214","B87","Top456"]; // MAKE SURE THIS ORDER MATCHES SWITCH STATEMENT IN ROBOT CONTAINER


function updatePath(evt) {
    if (document.querySelector(".path-dropdown:hover") != null) {
        evt.preventDefault()
        selectedPath = evt.target.innerHTML
        let dropupBtn = document.querySelector(".dropbtn")
        dropupBtn.innerHTML = selectedPath
    }
}

function resetAndAddDropdownListeners() {
    document.querySelector(".dropbtn").innerHTML = "Choose Path"
    var paths = document.querySelectorAll(".path-dropdown .dropup-content p")
    for (let i = 0; i < paths.length; i++) {
        let path = paths[i]
        path.onclick = updatePath
    }
}

function setPaths() {

    var content = document.querySelector(".dropup-content")
    content.innerHTML = ""

    pathNames.forEach((path) => {
        var option = document.createElement("p")
        option.appendChild(document.createTextNode(path))
        content.append(option)
    })

    resetAndAddDropdownListeners()
}

setPaths()


var toggleCamera = document.querySelector('#toggle-camera')
toggleCamera.onclick = () => {
    var cameraDiv = document.querySelector("#camera-streams")
    if (document.querySelector(".camera-stream") == null) {
        var cameraStreams = `<image class="camera-stream" src="http://10.6.70.11:1182/stream.mjpg"></image>
        <image class="camera-stream" src="http://10.6.70.11:1182/stream.mjpg"></image>` //TODO change second stream
        cameraDiv.insertAdjacentHTML( 'beforeend', cameraStreams )
    } else {
        while (cameraDiv.firstChild) {
            cameraDiv.removeChild(cameraDiv.firstChild)
        }
    }
}

// // listens for robot-state and updates status lights and auton chooser accordingly
// NetworkTables.addKeyListener('/SmartDashboard/robot-state', (key, value) => {
//     if (value === "autonomousInit()" || value === "disabledPeriodic()") {
//         document.getElementById('auton-chooser').style.display = "none"
//     } else if (value === "autonomousPeriodic()") {
//         document.getElementById('auton-status').style.fill = "rgb(0,255,0)"
//         document.getElementById('auton-status').style.stroke = "rgb(0,255,0)"
//     } else if (value === "teleopInit()" || value === "teleopPeriodic()") {
//         document.getElementById('auton-status').style.fill = "none"
//         document.getElementById('auton-status').style.stroke = "rgb(255,255,255)"
//     }
// })


NetworkTables.addKeyListener('/SmartDashboard/match-started', (key, value) => {
    var autoSelector = document.querySelector('#auton-chooser')
    if (value) {
        autoSelector.style.display = 'none'
    } else {
        autoSelector.style.display = 'block'
    }
})


NetworkTables.addKeyListener('/Shuffleboard/Drivetrain/Back Left Module/Absolute Encoder Angle', (key, value) => {
    var backLeftValue = document.querySelector("h3#backLeft-value")
    backLeftValue.innerHTML = "Back Left Absolute Position(deg): " + Number(value).toFixed(2)
    if (value == null){
        backLeftValue.backgroundColor = "#FF0000"; //red
    }else if(value >= -2 && value <= 2){
        backLeftValue.backgroundColor = "#00FF00"; //green
    }
})

NetworkTables.addKeyListener('/Shuffleboard/Drivetrain/Back Right Module/Absolute Encoder Angle', (key, value) => {
    var backRightValue = document.querySelector("h3#backRight-value")
    backRightValue.innerHTML = "Back Right Absolute Position(deg): " + Number(value).toFixed(2)
    if (value == null){
        backRightValue.backgroundColor = "#FF0000"; //red
    }else if(value >= -2 && value <= 2){
        backRightValue.backgroundColor = "#00FF00"; //green
    }
})

NetworkTables.addKeyListener('/Shuffleboard/Drivetrain/Front Left Module/Absolute Encoder Angle', (key, value) => {
    var frontLeftValue = document.querySelector("h3#frontLeft-value")
    frontLeftValue.innerHTML = "Front Left Absolute Position(deg): " + Number(value).toFixed(2)
    if (value == null){
        frontLeftValue.backgroundColor = "#FF0000"; //red
    }else if(value >= -2 && value <= 2){
        frontLeftValue.backgroundColor = "#00FF00"; //green
    }
})

NetworkTables.addKeyListener('/Shuffleboard/Drivetrain/Front Right Module/Absolute Encoder Angle', (key, value) => {
    var frontRightValue = document.querySelector("h3#frontRight-value")
    frontRightValue.innerHTML = "Front Right Absolute Position(deg): " + Number(value).toFixed(2)
    if (value == null){
        frontRightValue.backgroundColor = "#FF0000"; //red
    }else if(value >= -2 && value <= 2){
        frontRightValue.backgroundColor = "#00FF00"; //green
    }
})


NetworkTables.addKeyListener('/SmartDashboard/target-arm-state', (key, value) => {
    var armState = document.querySelector('div#arm-state h1#arm-state-text')
    armState.innerHTML = ""
    armState.append(document.createTextNode(value))
})

// // updates robot angle and direction
//     NetworkTables.addKeyListener('/SmartDashboard/vision-values-', (key, value) => {
//         document.getElementById('distance').textContent = 'Distance: ' + value[0]
//         document.getElementById('angle').textContent = 'Angle: ' + value[2]
//     })
NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Deployer/DeployerDeployed', (key, value) => {
    var subsystem = document.getElementById('intake')
    if(value == 'TRUE'){
        subsystem.style.x = '70%'
        subsystem.style.y = '47%'
        subsystem.style.width = '20%'
        subsystem.style.height = '15%'
        subsystem.style.rotate = '30deg'
    } else {
        subsystem.style.x = '45%'
        subsystem.style.y = '15%'
        subsystem.style.width = '20%'
        subsystem.style.height = '15%'
        subsystem.style.rotate = '30deg'
    }
    
})

NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Elevator/State', (key, value) => {
    var subsystem = document.getElementById('tilter')
    var subsystem2 = document.getElementById('shooter')
    if(value == 'AMP'){
        subsystem.style.cx = '38%'
        subsystem.style.cy = '35%'
        subsystem.style.r = '25'
        subsystem2.style.x = '23%'
        subsystem2.style.y = '35%'
        subsystem2.style.width = '20%'
        subsystem2.style.height = '18%'
    } else {
        subsystem.style.cx = '38%'
        subsystem.style.cy = '75%'
        subsystem.style.r = '25'
        subsystem2.style.x = '24%'
        subsystem2.style.y = '75%'
        subsystem2.style.width = '20%'
        subsystem2.style.height = '18%'
    }
    
})
// updates status lights for driveBase
NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Deployer/Health', (key, value) => {
    var subsystem = document.getElementById('deployer')
    if (value === 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value === 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value === 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})

NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Tilter/Health', (key, value) => {
    var subsystem = document.getElementById('tilter')
    if (value === 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value === 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value === 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})


NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Elevator/Health', (key, value) => {
    var subsystem = document.getElementById('elevator')
    if (value === 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value === 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value === 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})


NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Intake/Health', (key, value) => {
    var subsystem = document.getElementById('intake')
    if (value === 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value === 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value === 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})


NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Indexer/Health', (key, value) => {
    var subsystem = document.getElementById('indexer')
    if (value === 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value === 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value === 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})

NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/DriveBase/Health', (key, value) => {
    var subsystem = document.getElementById('drivebase')
    if (value == 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value == 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value == 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})

NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Shooter/Health', (key, value) => {
    var subsystem = document.getElementById('shooter')
    if (value === 'GREEN') {
        subsystem.style.fill = "rgb(0,255,0)"
    } else if (value === 'YELLOW') {
        subsystem.style.fill = "rgb(255,255,0)"
    } else if (value === 'RED') {
        subsystem.style.fill = "rgb(255,0,0)"
    }
})
NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Elevator/State', (key, value) => {
    var subsystem = document.getElementById('elevator-state')
    subsystem.textContent="Elevator State: " + value

})

NetworkTables.addKeyListener('/SmartDashboard/auton-chooser', (key, value) => {
    var currentPath = document.querySelector("#auto-form h3#current-path")
    var pathText = "Not Sent"
    var listenedAuto = Number(value)
    if (listenedAuto >= 0 && listenedAuto < pathNames.length) {
        pathText = pathNames[listenedAuto]
    }
    // switch (listenedAuto) { // Todo
    //     case 0: // ConeCube(driveBase, claw, arm, "CableScore")
    //         pathText = "cableScore"
    //         break
    //     case 1: // ConeCube(driveBase, claw, arm, "StationScore")
    //         pathText = "stationScore"
    //         break
    //     case 2: // CubeEngage(driveBase, claw, arm, "CableEngage")
    //         pathText = "cableEngage"
    //         break
    //     case 3: // CubeEngage(driveBase, claw, arm, "StationEngage")
    //         pathText = "stationEngage"
    //         break
    //     case 4: // CenterEngage(driveBase, claw, arm, "CenterEngage")
    //         pathText = "centerEngage"
    //         break
    //     case 5: // CenterEngage(driveBase, claw, arm, "CenterEngage")
    //         pathText = "centerIntake"
    //         break
    //     case 6:
    //         pathText = "scoreMid"
    //     default:
    //         pathText = "Not Sent"
    // }
    currentPath.innerHTML = ""
    currentPath.append("Current Path: " + pathText)
})

NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/LED/LedStatus', (key, value) => {
    var body = document.querySelector("body");
    var color = "OFF";
    switch (value) {
        case "AMP":
            color = "purple"
            break;
        case "VISIONON":
            color = "green"
            break;
        case "COOPERTITION":
            color = "yellow"
            break;
        case "INTAKING":
            color = "red"
            break;
        case "HASNOTE":
            color = "white"
            break;
        case "READYTOSHOOT":
            color = "blue"
            break;
        default: 
            color = "OFF";
            break;
    }
    console.log(color);
    if (color == "OFF") {
        body.style.border = "";
    } else {
        body.style.border = "30px solid " + color;
    }
})

NetworkTables.addKeyListener('/AdvantageKit/RealOutputs/Arducam_B_Connected', (key, value) => {
    var warning = document.querySelector("#big-warning")
    if (value) {
        warning.style.display = "none";
    } else {
        warning.style.display = "block";
    } 
})


document.getElementById("confirm-button").onclick = () => {
    sendAuton()
}



function getAutonFromMap() {
    console.log("SELECTED VALUE", selectedPath)
    // switch (selectedPath) {
    //     case "cableScore": // ConeCube(driveBase, claw, arm, "CableScore")
    //         return 0.0
    //     case "stationScore": // ConeCube(driveBase, claw, arm, "StationScore")
    //         return 1.0
    //     case "cableEngage": // CubeEngage(driveBase, claw, arm, "CableEngage")
    //         return 2.0
    //     case "stationEngage": // CubeEngage(driveBase, claw, arm, "StationEngage")
    //         return 3.0
    //     case "centerEngage": // CenterEngage(driveBase, claw, arm, "CenterEngage")
    //         return 4.0
    //     case "centerIntake": // CenterEngage(driveBase, claw, arm, "CenterEngage")
    //         return 5.0
    //     case "scoreMid":
    //         return 6.0
    //     default:
        
    //     return -1
    // }

    for (let i = 0; i < pathNames.length; i++) {
        if (pathNames[i] == selectedPath) {
            return i;
        }
    }
    return -1;
}

function getDelayTime() {
    return parseFloat(document.querySelector('#delay input[name="delay-time"]').value)
}


function sendAuton() {
    var autonCommand = getAutonFromMap()
    var autoSelectWarning = document.querySelector("div#auto-warning")
    if (autonCommand === -1) {
        autoSelectWarning.style.display = "block"
        return
    } else {
        autoSelectWarning.style.display = "none"
    }
    var delayTime = getDelayTime()
    console.log("SELECTED AUTON COMMAND", autonCommand)
    NetworkTables.putValue('/SmartDashboard/auton-chooser', autonCommand)
    NetworkTables.putValue('/SmartDashboard/delayTime', delayTime)
}