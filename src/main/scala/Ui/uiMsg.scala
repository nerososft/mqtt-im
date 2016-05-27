package Ui

import java.util.Date
import java.text.SimpleDateFormat
import javafx.collections.{FXCollections, ObservableList}

import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView, TextField}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.text.Text
import scalafx.Includes._
import scalafx.scene.effect.InnerShadow
import scalafx.stage.{Stage, StageStyle}

/**
  * Created by NeroYang on 16-5-8.
  */
object uiMsg extends Stage {
  var userid: String = "";
  var password: String = "";

  var msgs: ObservableList[String] = FXCollections.observableArrayList()
  var msglist: ListView[String] = new ListView[String](msgs)
  var myname: Text = new Text()

  var sendTo: TextField = new TextField()
  var msgContent: TextField = new TextField()
  var bt_send: Button = new Button()

  def initUi(): Unit = {
    myname = new Text {
      fill = new LinearGradient(
        endX = 0,
        stops = Stops(White, White)
      )
    }

    msgs = FXCollections.observableArrayList()
    msglist = new ListView[String](msgs) {
      items = msgs
      prefWidth = 290
      prefHeight = 300
      margin = Insets(30, 0, 0, 0)
      val innerShadow: InnerShadow = new InnerShadow()
      innerShadow.setOffsetX(0)
      innerShadow.setOffsetY(0)
      innerShadow.setColor(Color.web("0x88f"))
      effect = (innerShadow)
    }
    msgContent = new TextField {
      promptText = "msg:"
      prefWidth = 200
    }
    bt_send = new Button {
      prefWidth = 50
      text = "发送"

    }
  }

  def render(totopic:String,userid: String, password: String): Unit = {

    val msgtest: msgTest = new msgTest()
    if (msgtest.userid.equals("")) {
      msgtest.useridset(userid)
    }
    if (msgtest.password.equals("")) {
      msgtest.passwordset(password)
    }
    initUi
    myname.setText("myname:" + userid + "   mypwd:" + password)
    msglist.setItems(msgs)
    msgtest.mqttInit() //init mqttclient
    msgtest.listen()
    bt_send.onMouseClicked = (event: MouseEvent) => {
      if (msgContent.text().equals("")) {

      } else {
        msgtest.toset(totopic)
        msgtest.msgset(msgContent.text())
        msgtest.send()
      }
    }
    new PrimaryStage {
      title = "与"+totopic+"聊天中"
      width = 400
      height = 600
      scene = new Scene {
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(DodgerBlue, DodgerBlue)
        )
        content = new VBox() {
          padding = Insets(5)
          children = List(
            myname, msglist,
            new HBox {
              children = List(msgContent, bt_send)
            }
          )
        }
      }
    }
  }
}


case class msgTest() {
  //println(uiMsg.msgs)
  //uiMsg.msglist.setItems(uiMsg.msgs)
  println("消息测试---------------------------------------------")
  var userid: String = ""
  var password: String = ""
  var topic: String = ""
  var content: String = ""
  var msg: String = ""
  val qos: Int = 1
  val broker: String = "tcp://0.0.0.0:61613"
  val clientId: String = ""
  val persistence: MemoryPersistence = new MemoryPersistence()
  var sampleClient: MqttClient = null
  val connOpts: MqttConnectOptions = new MqttConnectOptions()
  connOpts.setCleanSession(false)
  //设置会话心跳时间
  connOpts.setKeepAliveInterval(18330);
  var isConnected: Boolean = false

  def toset(to: String): Unit = {
    this.topic = "/msg/user/" + to
  }

  def msgset(msg: String): Unit = {
    this.content = msg
  }

  def useridset(userid: String): Unit = {
    this.userid = userid
  }

  def passwordset(password: String): Unit = {
    this.password = password
  }

  def getmsg(): String = {
    this.msg
  }

  def mqttInit(): Unit = {
    connOpts.setUserName(this.userid)
    connOpts.setPassword(this.password.toCharArray)
    sampleClient = new MqttClient(broker, this.userid, persistence)
    sampleClient.setCallback(new callBack)
    if (!sampleClient.isConnected) {
      sampleClient.connect(connOpts)
    }
    println(" 连接到: " + broker)
    isConnected = true
  }

  def send(): Unit = {
    try {

      val message: MqttMessage = new MqttMessage(content.getBytes)
      message.setQos(qos)
      sampleClient.publish(this.topic, message)
      println("发送到" + this.topic + " 内容:" + content)

    } catch {
      case me: MqttException => {
        println("reason " + me.getReasonCode())
        println("msg " + me.getMessage())
        println("loc " + me.getLocalizedMessage())
        println("cause " + me.getCause())
        println("excep " + me)
        me.printStackTrace()
      }
    }
  }

  def listen(): Unit = {
    try {
      println("订阅主题： /msg/user/" + this.userid)
      sampleClient.subscribe("/msg/user/" + this.userid, 0)

    } catch {
      case me: MqttException => {
        println("reason " + me.getReasonCode())
        println("msg " + me.getMessage())
        println("loc " + me.getLocalizedMessage())
        println("cause " + me.getCause())
        println("excep " + me)
        me.printStackTrace()
      }
    }
  }
}

class callBack extends MqttCallback {
  override def deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken): Unit = println(iMqttDeliveryToken)

  override def messageArrived(s: String, mqttMessage: MqttMessage): Unit = {
    println("收到消息:" + mqttMessage.toString)
    uiMsg.myname.setText("最新消息:" + mqttMessage.toString)
    val now: Date = new Date();
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("MM月dd HH:mm:ss"); //可以方便地修改日期格式

    uiMsg.msgs.add(uiMsg.msgs.size(), dateFormat.format(now) + ":" + mqttMessage.toString)
    uiMsg.msglist.setItems(uiMsg.msgs)
  }

  override def connectionLost(throwable: Throwable): Unit = println("!!!!!!!!!掉线!!!!!!!!")
}