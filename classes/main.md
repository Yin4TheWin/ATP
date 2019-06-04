# Main

## Life cycle

I think this is a good time to discuss the life-cycle of this project:

```
Main -> MainMenuScreen -> GameScreen <-> BattleScreen
```

{% hint style="info" %}
 Other classes, such as the player class, are called from Game and Battle Screen but are independent of the game's life cycle.
{% endhint %}

I believe Main.java is small enough for me to just copy paste here:

{% code-tabs %}
{% code-tabs-item title="Main.java" %}
```java
public class Main extends Game {

	public SpriteBatch batch;
	public BitmapFont font;

	public void create() {
	    //A group of sprites
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();
		//Go to Main Menu class
		this.setScreen(new MainMenuScreen(this));
	}
//Don't worry about everything below this
	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}
```
{% endcode-tabs-item %}
{% endcode-tabs %}

Let's go over what everything does. I use SpriteBatch and BitmapFont here, which you can read up on with the links I provide at the bottom of this page. Main extends Game, which you can also read about below. I instantiate everything in the create class and immediately set the screen to MainMenuScreen. Render is for rendering things on screen and dispose disposes of things when the activity finishes.

Further Reading:

{% embed url="https://github.com/libgdx/libgdx/wiki/Extending-the-simple-game" caption="Reading this is highly recommended. It will teach you all the basics." %}



{% embed url="https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/Batch.html" %}

{% embed url="https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/BitmapFont.html" %}

