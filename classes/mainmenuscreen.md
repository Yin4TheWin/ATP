# MainMenuScreen

## The Code

Here's most of the code, some was deprecated to avoid unnecessary clutter:

```java
public class MainMenuScreen implements Screen {
    //Main is an object, it's kinda like the game itself if that makes sense
    final Main game;

    //Camera makes sure screen will always be certain size
    OrthographicCamera camera;

    public MainMenuScreen(final Main game) {
        //Don't worry too much about this
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 832, 512);

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        //Clears screen and makes it dark blue
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Refreshes screen to show updated sprites, I believe its 60 fps
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        //Actual drawing happens here
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to the Game! ", 305, 340);
        game.font.draw(game.batch, "Click the screen to begin.", 300, 240);
        game.batch.end();

        //Like an onClick
        if (Gdx.input.isTouched()) {
            //Goes to the GameScreen Class
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
}
```

{% hint style="info" %}
 You can view the whole thing at the Github repo.
{% endhint %}

As you can see, the constructor takes a "Main", which we covered in the previous article when we used setScreen\(this\). The "this" passed the current instance of "Main" to this class. Declarations \(like the OrthographicCamera\) will not be covered here but I will link articles from the API or their wiki for further reading. Let's get into some specific lines of code:

```java
Gdx.gl.glClearColor(0, 0, 0.2f, 1);
Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
```

This clears the screen every time render is run. So new drawings aren't overlaid with the previous ones.

{% hint style="warning" %}
 IMPORTANT: How often render runs is dependent on the speed of your computer. To get an object  to move at x pixels per second we use x \* Gdx.graphics.getDeltaTime\(\) . Neglecting Gdx.graphics.getDeltaTime\(\) will result in different move speeds for different computers.
{% endhint %}

```java
 game.batch.begin();
 game.font.draw(game.batch, "Welcome to the Game! ", 305, 340);
 game.font.draw(game.batch, "Click the screen to begin.", 300, 240);
 game.batch.end();
```

All "draw" methods must be called between a batch.begin\(\) and a batch.end\(\) to avoid errors.

```java
if (Gdx.input.isTouched()) {      
   game.setScreen(new GameScreen(game));
   dispose();
}
```

If the screen is clicked, touched, or tapped we will dispose and set the screen to game screen. Note that this time we are not using "this" but rather we are passing GameScreen a "game". Don't forget that "game" is the same as "this" from the previous time we called setScreen\(\). Since it was passed to this class, MainMenuScreen, we are simply passing it again to a new class, GameScreen. Both setScreen and dispose were covered in the previous article.

Further Reading:

{% embed url="https://github.com/libgdx/libgdx/wiki/Orthographic-camera" caption="Orthographic Camera" %}



