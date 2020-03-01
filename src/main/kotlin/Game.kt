import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.math.abs


class Game : Application() {
    private val keyboardInputs = ArrayList<String>()
    private var gameOver = true
    var runner = Player(DEFAULT_PLAYER_POS_X, DEFAULT_PLAYER_POS_Y)
    var banana = Obstacle(DEFAULT_OBSTACLE_POS_X, DEFAULT_OBSTACLE_POS_Y)

    companion object {
        const val WIDTH = 1000.0
        const val HEIGHT = 680.0
        const val DEFAULT_PLAYER_POS_X = 300.0
        const val DEFAULT_PLAYER_POS_Y = 350.0
        const val DEFAULT_OBSTACLE_POS_X = 930.0
        const val DEFAULT_OBSTACLE_POS_Y = 470.0
        const val HIGHSCORE_POS_X = 420.0
    }

    override fun start(stage: Stage) {
        val root = Group()
        val scene = Scene(root)
        val canvas = Canvas(WIDTH, HEIGHT)
        val gc = canvas.graphicsContext2D
        val startButton = Button("Start game")
        val highscoreButton = Button("Highscores")
        val background = Image("background.jpg")
        val startNanoTime = System.nanoTime()
        var highscore = 0.0
        val highscoreLabel = Label("Highscore: ${highscore.toInt()} m")

        // set the screen
        stage.scene = scene
        stage.title = "Kotlin game"
        stage.isResizable = false
        root.children.add(canvas)
        root.children.add(highscoreLabel)
        root.children.add(startButton)
        root.children.add(highscoreButton)

        // set highscorelabel's properties
        highscoreLabel.isVisible = true
        highscoreLabel.relocate(HIGHSCORE_POS_X, 0.0)
        highscoreLabel.font = Font("Arial", 20.0)

        // set button properties
        setButtonsStyle(startButton, highscoreButton)

        // add keyboard events
        scene.setOnKeyReleased { event ->
            if (!keyboardInputs.contains(event.code.toString()))
                keyboardInputs.add(event.code.toString())
        }

        // add button events
        startButton.setOnMouseClicked {
            gameOver = false
            highscore = 0.0
            startButton.isVisible = false
            highscoreButton.isVisible = false
        }
        highscoreButton.setOnMouseClicked {
            // TODO: handle highscorebutton
        }

        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                val t = (currentNanoTime - startNanoTime) / 1000000000.0

                runner.calculatePos(keyboardInputs)
                banana.calculatePos(keyboardInputs)

                gc.drawImage(background, 0.0, 0.0)
                runner.draw(gc, t, gameOver)
                banana.draw(gc, t, gameOver)

                // game ended
                if (detectCollision()) {
                    gameOver = true
                    startButton.isVisible = true
                    highscoreButton.isVisible = true
                    runner = Player(DEFAULT_PLAYER_POS_X, DEFAULT_PLAYER_POS_Y)
                    banana = Obstacle(DEFAULT_OBSTACLE_POS_X, DEFAULT_OBSTACLE_POS_Y)
                }
                // game not over yet, increment highscore
                else if (!gameOver) {
                    highscore += 0.3
                    highscoreLabel.text = "Highscore: ${highscore.toInt()} m"
                }
            }
        }.start()
        stage.show()
    }

    // fun to detect collision
    private fun detectCollision(): Boolean {
        var collision = false
        if (abs(banana.posX - runner.posX) < 30 && abs(banana.posY - runner.posY) < 130)
            collision = true
        return collision
    }

    private fun setButtonsStyle(startButton: Button, highscoreButton: Button) {
        startButton.setMinSize(200.0, 50.0)
        startButton.relocate(600.0, 350.0)
        startButton.style = "-fx-background-color: \n" +
                "        linear-gradient(#f2f2f2, #d6d6d6),\n" +
                "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
                "        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
                "    -fx-background-radius: 8,7,6;\n" +
                "    -fx-background-insets: 0,1,2;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"


        highscoreButton.setMinSize(200.0, 50.0)
        highscoreButton.relocate(600.0, 450.0)
        highscoreButton.style = "-fx-background-color: \n" +
                "        linear-gradient(#f2f2f2, #d6d6d6),\n" +
                "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
                "        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
                "    -fx-background-radius: 8,7,6;\n" +
                "    -fx-background-insets: 0,1,2;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
    }
}