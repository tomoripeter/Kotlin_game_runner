import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode


class Player(override var posX: Double, override var posY: Double) : IDrawable {
    private val image = AnimatedImage(0.07)
    private var jumpLoop = 20
    private var moveLoop = 0

    init {
        image.images.add(Image("runningman1.jpg"))
        image.images.add(Image("runningman2.jpg"))
        image.images.add(Image("runningman3.jpg"))
        image.images.add(Image("runningman4.jpg"))
    }

    override fun draw(gc: GraphicsContext, time: Double, gameOver: Boolean) {
        if (gameOver)
            gc.drawImage(image.images[3], posX, posY)
        else
            gc.drawImage(image.getAnimatedImage(time), posX, posY)
    }

    override fun calculatePos(keyboardInputs: ArrayList<String>) {
        // calculate Y position
        if (keyboardInputs.contains(KeyCode.UP.toString())) {
            jumpLoop--
            posY = Game.DEFAULT_PLAYER_POS_Y - jumpLoop * 4
            if (jumpLoop == 0) {
                jumpLoop = 20
                keyboardInputs.remove(KeyCode.UP.toString())
            }
        } else
            posY = Game.DEFAULT_PLAYER_POS_Y

        // calculate X positon
        if (keyboardInputs.contains(KeyCode.LEFT.toString())) {
            moveLoop++
            posX = if (posX < 0.0) posX else posX - moveLoop
            if (moveLoop == 10) {
                moveLoop = 0
                keyboardInputs.remove(KeyCode.LEFT.toString())
            }
        }
        if (keyboardInputs.contains(KeyCode.RIGHT.toString())) {
            moveLoop++
            posX = if (posX > 1000.0) posX else posX + moveLoop
            if (moveLoop == 10) {
                moveLoop = 0
                keyboardInputs.remove(KeyCode.RIGHT.toString())
            }
        }
    }
}