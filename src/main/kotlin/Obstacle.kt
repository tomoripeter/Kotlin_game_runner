import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import kotlin.random.Random

class Obstacle(override var posX: Double, override var posY: Double) : IDrawable {
    private val image = Image("peeledbanana.jpg")
    private var speed = 0.0

    override fun draw(gc: GraphicsContext, time: Double, gameOver: Boolean) {
        if (gameOver)
            speed = 0.0
        else
            calculateSpeed()
        gc.drawImage(image, posX, posY)
    }

    override fun calculatePos(keyboardInputs: ArrayList<String>) {
        posX = if (posX - speed < 0) Game.DEFAULT_OBSTACLE_POS_X else posX - speed
    }

    private fun calculateSpeed() {
        if (posX == Game.DEFAULT_OBSTACLE_POS_X)
            speed = 5 + Random.nextDouble(0.0, 10.0)
    }
}