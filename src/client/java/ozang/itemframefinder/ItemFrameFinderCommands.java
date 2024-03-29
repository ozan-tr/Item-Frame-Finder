package ozang.itemframefinder;



import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import com.mojang.brigadier.arguments.StringArgumentType;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemFrameFinderCommands {


    public void registerCommands(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess)->
			dispatcher.register(literal("find")
				.then(literal("clear")
					.executes(context -> {
						ItemFinder.searchItems.clear();
						context.getSource().sendFeedback(Text.literal("Cleared the list"));
						return 1;
					})
				).then(literal("list")
					.executes(context -> {
						context.getSource().sendFeedback(Text.literal("List of items: " + ItemFinder.searchItems.toString()));
						return 1;
					})
				).then(literal("add")
					.then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
						.executes(context -> {
							Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
							if(item == null){
								context.getSource().sendError(Text.literal("Item not found"));
								return -1;
							}
							if(ItemFinder.searchItems.contains(item)){
								context.getSource().sendError(Text.literal("Item already in the list"));
								return -1;
							}
							ItemFinder.searchItems.add(item);
							context.getSource().sendFeedback(Text.literal("Added " + item.toString() + " to the list"));
							return 1;
						})
					)
				).then(literal("remove")
					.then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
						.executes(context -> {
							Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
							if(item == null){
								context.getSource().sendError(Text.literal("Item not found"));
								return -1;
							}
							if(!ItemFinder.searchItems.contains(item)){
								context.getSource().sendError(Text.literal("Item not in the list"));
								return -1;
							}
							ItemFinder.searchItems.remove(item);
							context.getSource().sendFeedback(Text.literal("Removed " + item.toString() + " from the list"));
							return 1;
						})
					)
				).then(literal("preset")
					.then(literal("list")
						.executes(context -> {
							ItemFrameFinderConfig config = AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).getConfig();
							String presetNames = "";
							for(ItemFrameFinderConfig.Preset preset : config.presetsPanel){
								presetNames += preset.name + ", ";
							}
							context.getSource().sendFeedback(Text.literal("List of presets: " + presetNames));
							return 1;
						})
					).then(literal("load")
						.then(argument("preset", StringArgumentType.string()).suggests(new ItemFinderCommandSuggestion())
						.executes(context -> {
							String presetName = StringArgumentType.getString(context, "preset");
							ItemFrameFinderConfig config = AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).getConfig();
							for(ItemFrameFinderConfig.Preset preset : config.presetsPanel){
								if(preset.name.equals(presetName)){
									ItemFinder.searchItems.clear();
									for(String item : preset.items){
										Registries.ITEM.getOrEmpty(new Identifier(item)).ifPresentOrElse(ItemFinder.searchItems::add, () -> {
											context.getSource().sendError(Text.literal("Item not found: " + item));
										});
									}
									context.getSource().sendFeedback(Text.literal("Loaded preset: " + presetName));
									return 1;
								}
							}
							context.getSource().sendError(Text.literal("Preset not found"));
							return -1;
						})
						)).then(literal("save")
							.then(argument("preset", StringArgumentType.string())
								.executes(context -> {
									String presetName = StringArgumentType.getString(context, "preset");
									ItemFrameFinderConfig config = AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).getConfig();

									for(ItemFrameFinderConfig.Preset preset : config.presetsPanel){
										if(preset.name.equals(presetName)){
											preset.items = ItemFinder.searchItems.stream().map(Item::toString).toList();
											AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).save();
											context.getSource().sendFeedback(Text.literal("Preset changed"));
											return 1;
										}
									}

									config.presetsPanel.add(new ItemFrameFinderConfig.Preset(presetName, ItemFinder.searchItems.stream().map(Item::toString).toList()));
									AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).save();
									context.getSource().sendFeedback(Text.literal("Saved preset: " + presetName));
									return 1;
								})
							)
				).then(literal("remove")
				.then(argument("preset", StringArgumentType.string())
				.suggests(new ItemFinderCommandSuggestion())
					.executes(context -> {
						String presetName = StringArgumentType.getString(context, "preset");
						ItemFrameFinderConfig config = AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).getConfig();
						for(ItemFrameFinderConfig.Preset preset : config.presetsPanel){
							if(preset.name.equals(presetName)){
								config.presetsPanel.remove(preset);
								AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).save();
								context.getSource().sendFeedback(Text.literal("Removed preset: " + presetName));
								return 1;
							}
						}
						context.getSource().sendError(Text.literal("Preset not found"));
						return -1;
					})
				)
			))
				.executes(context -> {
					context.getSource().sendFeedback(Text.literal("Usage: /find <clear|list|add|remove|preset>"));
					return 1;
				}
			))
		);

    }
}
