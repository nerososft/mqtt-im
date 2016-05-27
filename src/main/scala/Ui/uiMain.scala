package Ui
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ObservableList}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.text.Text
import scalafx.Includes._
import scalafx.scene.effect.InnerShadow
import scalafx.scene.image.{Image, ImageView}

/**
  * Created by root on 16-5-11.
  */
object uiMain extends JFXApp {
  var avatar: Image = new Image("img/break.png")
  var avatarView: ImageView = new ImageView()

  var usernameText: Text = new Text()
  var clientidText: Text = new Text()

  var friends: ObservableList[String] = FXCollections.observableArrayList()
  var friendsList: ListView[String] = new ListView[String](friends)

  def initUi(): Unit = {
    avatar = new Image("img/break.png")
    avatarView = new ImageView()
    avatarView.setImage(avatar)
    avatarView.setFitHeight(60)
    avatarView.setFitWidth(60)
    avatarView.prefWidth(60)
    avatarView.prefHeight(60)

    usernameText = new Text()
    clientidText = new Text()
    usernameText.setText("这里是用户名")
    clientidText.setText("这里是clientid")

    friends = FXCollections.observableArrayList("sb1","sb2")
    friendsList = new ListView[String](friends) {
      items = friends
      prefWidth = 290
      prefHeight = 300
      margin = Insets(30, 0, 0, 0)
      val innerShadow: InnerShadow = new InnerShadow()
      innerShadow.setOffsetX(0)
      innerShadow.setOffsetY(0)
      innerShadow.setColor(Color.web("0x88f"))
      effect = (innerShadow)
    }
  }

  def render(clientid: String, password: String): Unit = {
    initUi
    friendsList.getSelectionModel.selectedItemProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        uiMsg.render(newValue,clientid,password)
      }
    })
    usernameText.setText(clientid)
    clientidText.setText("welcome")
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
          padding = Insets(5)
          children = List(
            new HBox {
              children = List(
                avatarView,
                new VBox {
                  padding = Insets(10)
                  children = List(usernameText, clientidText)
                }
              )
            }, friendsList
          )
        }
      }
    }
  }
}
case class Main(){

}
