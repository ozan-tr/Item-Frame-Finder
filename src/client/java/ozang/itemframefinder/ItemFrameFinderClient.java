package ozang.itemframefinder;

import org.lwjgl.glfw.GLFW;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;



public class ItemFrameFinderClient implements ClientModInitializer {

	private static KeyBinding activateFinder;
	
	private int keyCoolDown = 0;
	private int searchCooldown = 0;

	private boolean isFinderActive = false;




	@Override
	public void onInitializeClient() {



		AutoConfig.register(ItemFrameFinderConfig.class, GsonConfigSerializer::new);

		activateFinder = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.item-frame-finder.toggle", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_H, // The keycode of the key
				"category.item-frame-finder.bind" // The translation key of the keybinding's category.
		));

		ItemFrameFinderCommands itemFrameFinderCommands = new ItemFrameFinderCommands();
		itemFrameFinderCommands.registerCommands();

		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (this.keyCoolDown > 0) {
				this.keyCoolDown--;
			}

			if (this.searchCooldown > 0) {
				this.searchCooldown--;
			}

			if(activateFinder.isPressed() && this.keyCoolDown == 0){
				this.keyCoolDown = 5;
				isFinderActive = !isFinderActive;

				if(isFinderActive){
					client.player.sendMessage(Text.of("Item Frame Finder Activated"), true);
				}else{
					client.player.sendMessage(Text.of("Item Frame Finder Deactivated"), true);
				}
			}

			if (isFinderActive && this.searchCooldown <= 0) {
				this.searchCooldown = 20;
				if(client.player != null){
					if(ItemFinder.searchItems.isEmpty()){
						client.player.sendMessage(Text.of("No items to search for (/find add)"), true);
						return;
					}else{
						ItemFinder.FindMatchingFrames(client.player);
					}
				}
			}
		});
	}

}