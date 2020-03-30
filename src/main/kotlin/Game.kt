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
import java.io.File
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
        const val BUTTONS_POS_X = 600.0
        const val BUTTONS_WIDTH = 200.0
        const val BUTTONS_HEIGHT = 50.0
    }

    override fun start(stage: Stage) {
        val root = Group()
        val scene = Scene(root)
        val canvas = Canvas(WIDTH, HEIGHT)
        val gc = canvas.graphicsContext2D
        val file = File("highscore.txt")
        val startButton = Button("Start game")
        val highscoreButton = Button("Highscores")
        val retHighscoreButton = Button("Back to menu")
        val background = Image("background.jpg")
        val top3 = Image("top3.png")
        val startNanoTime = System.nanoTime()
        var highscore = 0.0
        val highscoreLabel = Label("Highscore: ${highscore.toInt()} m")
        val bestScoreLabel = Label("")
        var showHighscore = false

        // set the screen
        stage.scene = scene
        stage.title = "Kotlin game"
        stage.isResizable = false
        root.children.add(canvas)
        root.children.add(highscoreLabel)
        root.children.add(startButton)
        root.children.add(highscoreButton)
        root.children.add(retHighscoreButton)
        root.children.add(bestScoreLabel)

        // set highscorelabel's properties
        highscoreLabel.isVisible = true
        highscoreLabel.relocate(HIGHSCORE_POS_X, 0.0)
        highscoreLabel.font = Font("Arial", 20.0)

        bestScoreLabel.isVisible = false
        bestScoreLabel.font = Font("Arial", 20.0)

        // set button properties
        setButtonsStyle(startButton, highscoreButton, retHighscoreButton)

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
            if (!file.exists()) {
                file.createNewFile()
            }
            val readResult = file.bufferedReader().readLines()
            val intResult = ArrayList<Int>()
            for (result in readResult)
                intResult.add(result.toInt())
            intResult.sort()
            intResult.reverse()
            bestScoreLabel.isVisible = true
            highscoreButton.isVisible = false
            startButton.isVisible = false
            retHighscoreButton.isVisible = true
            showHighscore = true
            when (intResult.size) {
                0 -> {
                    bestScoreLabel.text = "0 m         0 m         0 m"
                    bestScoreLabel.relocate(BUTTONS_POS_X + BUTTONS_WIDTH / 2 - 100 , 425.0)
                }
                1 -> {
                    bestScoreLabel.text = "0 m        ${intResult[0]} m       0 m"
                    bestScoreLabel.relocate(BUTTONS_POS_X + BUTTONS_WIDTH / 2 - 100 , 425.0)
                }
                2 -> {
                    bestScoreLabel.text = "${intResult[1]} m     ${intResult[0]} m      0 m"
                    bestScoreLabel.relocate(BUTTONS_POS_X + BUTTONS_WIDTH / 2 - 100, 425.0)
                }
                else -> {
                    bestScoreLabel.text = "${intResult[1]} m     ${intResult[0]} m     ${intResult[2]} m"
                    bestScoreLabel.relocate(BUTTONS_POS_X + BUTTONS_WIDTH / 2 - 100, 425.0)
                }
            }
        }

        retHighscoreButton.setOnMouseClicked {
            bestScoreLabel.isVisible = false
            retHighscoreButton.isVisible = false
            highscoreButton.isVisible = true
            startButton.isVisible = true
            showHighscore = false
        }

        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                val t = (currentNanoTime - startNanoTime) / 1000000000.0

                runner.calculatePos(keyboardInputs)
                banana.calculatePos(keyboardInputs)

                gc.drawImage(background, 0.0, 0.0)
                runner.draw(gc, t, gameOver)
                banana.draw(gc, t, gameOver)
                if (showHighscore)
                    gc.drawImage(top3, BUTTONS_POS_X + BUTTONS_WIDTH / 2 - (top3.width) / 2, 300.0)

                // game ended
                if (detectCollision()) {
                    gameOver = true
                    if (!file.exists())
                        file.createNewFile()
                    file.appendText(highscore.toInt().toString() + '\n')
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

    private fun setButtonsStyle(startButton: Button, highscoreButton: Button, retHighscoreButton : Button) {
        startButton.setMinSize(BUTTONS_WIDTH, BUTTONS_HEIGHT)
        startButton.relocate(BUTTONS_POS_X, 350.0)
        startButton.style = "-fx-background-color: \n" +
                "        linear-gradient(#f2f2f2, #d6d6d6),\n" +
                "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
                "        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
                "    -fx-background-radius: 8,7,6;\n" +
                "    -fx-background-insets: 0,1,2;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"


        highscoreButton.setMinSize(BUTTONS_WIDTH, BUTTONS_HEIGHT)
        highscoreButton.relocate(BUTTONS_POS_X, 450.0)
        highscoreButton.style = "-fx-background-color: \n" +
                "        linear-gradient(#f2f2f2, #d6d6d6),\n" +
                "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
                "        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
                "    -fx-background-radius: 8,7,6;\n" +
                "    -fx-background-insets: 0,1,2;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"

        retHighscoreButton.setMinSize(BUTTONS_WIDTH, BUTTONS_HEIGHT)
        retHighscoreButton.relocate(BUTTONS_POS_X, 500.0)
        retHighscoreButton.style = "-fx-background-color: \n" +
                "        linear-gradient(#f2f2f2, #d6d6d6),\n" +
                "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
                "        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
                "    -fx-background-radius: 8,7,6;\n" +
                "    -fx-background-insets: 0,1,2;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
        retHighscoreButton.isVisible = false
    }
}