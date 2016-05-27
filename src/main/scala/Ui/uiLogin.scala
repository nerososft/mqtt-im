import Ui.uiMain
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions, MqttException}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.effect.{ DropShadow, InnerShadow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.text.Text
import scalafx.Includes._


object uiLogin extends JFXApp {
  def effectConnect(): Unit = {
    msg.visible = false
    bt_login.text = "connecting..."
    bt_login.disable = true
    userid.visible = false
    userid.prefHeight = 0
    password.prefHeight = 0
    password.visible = false
    login_process.margin = Insets(0, 0, 0, 20)
    login_process.style = "-fx-base:#fff"
    val innerShadow: InnerShadow = new InnerShadow()
    innerShadow.setOffsetX(100)
    innerShadow.setOffsetY(100)
    innerShadow.setColor(Color.web("0xfff"))
    login_process.setEffect(innerShadow)
    login_process.visible = true
    login_process.prefHeight = 60
  }

  def effectConnectFailed(failedMsg: String): Unit = {
    msg.text = failedMsg
    msg.visible = true
    login_process.visible = false
    login_process.prefHeight = 0
    bt_login.text = "LOGIN"
    bt_login.disable = false
    userid.visible = true
    userid.prefHeight = 30
    password.prefHeight = 30
    password.visible = true
    login_process.margin = Insets(0, 0, 0, 20)
  }

  val msg = new Text {
    fill = new LinearGradient(
      endX = 0,
      stops = Stops(White, White)
    )
    margin = Insets(0, 0, 0, 30)
    visible = false
  }
  val password = new PasswordField() {
    margin = Insets(5, 0, 0, 30)
    prefWidth = 195
    promptText = "password"
  }
  val userid = new TextField {
    margin = Insets(5, 0, 0, 30) //(top,right,bottom,left)
    prefWidth = 195
    promptText = "lebanid"
  }
  val bt_login = new Button {
    prefWidth = 92.5
    text = "LOGIN"
    textFill = new LinearGradient(
      endX = 0,
      stops = Stops(DodgerBlue, DodgerBlue)
    )
    style = "-fx-base:#fff"
  }
  val bt_cancel = new Button {
    prefWidth = 92.5
    margin = Insets(0, 0, 0, 10)
    text = "CANCEL"
    textFill = new LinearGradient(
      endX = 0,
      stops = Stops(DodgerBlue, DodgerBlue)
    )
    style = "-fx-base:#fff"

  }
  val login_process = new ProgressIndicator {
    progress = -1
    visible = false
    prefHeight = 0
  }
  bt_login.onMouseClicked = (event: MouseEvent) => {
    if (userid.text.equals("") || password.text().equals("")) {

    } else {
      Login(userid.text(), password.text())

    }
  }
  bt_cancel.onMouseClicked = (event: MouseEvent) => {
    System.exit(0)
  }
  password.onMouseClicked = (event: MouseEvent) => {
    msg.visible = false
  }
  stage = new PrimaryStage {
    //initStyle(StageStyle.Utility)
    title = "I'm"
    maxWidth = 300
    maxHeight = 500
    width = 300
    height = 500
    scene = new Scene {
      fill = new LinearGradient(
        endX = 0,
        stops = Stops(DodgerBlue, DodgerBlue)
      )

      content = new VBox() {
        padding = Insets(20)
        children = List(
          new HBox {
            children = List(
              new Text {
                margin = Insets(40, 0, 0, 80)
                text = "I'm"
                style = "-fx-font-size: 48pt"
                fill = new LinearGradient(
                  endX = 0,
                  stops = Stops(DodgerBlue, DodgerBlue)
                )
                effect = new DropShadow {
                  color = White
                  radius = 5
                  spread = 0.99
                }
              })
          },
          new VBox {
            margin = Insets(30, 0, 0, 0)
            children = List(
              login_process,
              msg,
              new HBox {
                children = List(
                  userid
                )
              }, new HBox {
                children = List(
                  password
                )
              })
          },
          new HBox {
            padding = Insets(30, 0, 0, 30)
            children = List(
              bt_login,
              bt_cancel
            )
          },
          new HBox {
            children = List(
              new Text {
                margin = Insets(115, 0, 0, 60)
                fill = new LinearGradient(
                  endX = 0,
                  stops = Stops(White, White)
                )
                text = "Leban.allRightReserved"
              }
            )
          }
        )

      }
    }
  }
}

case class Login(userid: String, password: String) {
  println("Login---------------------------------------------")
  val topic: String = "1"
  val content: String = "hello,im" + userid
  val qos: Int = 2
  val broker: String = "tcp://0.0.0.0:61613"
  val clientId: String = userid
  val persistence: MemoryPersistence = new MemoryPersistence()

  try {
    val sampleClient: MqttClient = new MqttClient(broker, clientId, persistence)
    val connOpts: MqttConnectOptions = new MqttConnectOptions()
    connOpts.setCleanSession(true)
    connOpts.setUserName(userid)
    val pwd = password.toCharArray
    //val pwd = Array('p','a','s','s','w','o','r','d')
    connOpts.setPassword(pwd)
    uiLogin.effectConnect
    println("Connecting to broker: " + broker)
    sampleClient.connect(connOpts)
    println("Connected to broker: " + broker)
    uiMain.render(userid, password)
  } catch {
    case me: MqttException => {
      println("reason " + me.getReasonCode())
      println("msg " + me.getMessage())
      uiLogin.effectConnectFailed(me.getMessage)
      println("loc " + me.getLocalizedMessage())
      println("cause " + me.getCause())
      println("excep " + me)
      me.printStackTrace()
    }
  }
}