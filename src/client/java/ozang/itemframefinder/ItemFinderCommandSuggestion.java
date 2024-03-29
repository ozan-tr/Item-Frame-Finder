package ozang.itemframefinder;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ItemFinderCommandSuggestion implements SuggestionProvider<FabricClientCommandSource> {
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        ItemFrameFinderConfig config = AutoConfig.getConfigHolder(ItemFrameFinderConfig.class).getConfig();

		// Thankfully, the ServerCommandSource has a method to get a list of player names.
		Collection<String> presetNames = config.presetsPanel.stream().map(preset -> preset.name).toList();

		// Add all player names to the builder.
		for (String presetName : presetNames) {
			builder.suggest(presetName);
		}

		// Lock the suggestions after we've modified them.
		return builder.buildFuture();
	}
}