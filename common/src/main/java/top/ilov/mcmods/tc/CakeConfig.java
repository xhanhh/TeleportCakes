package top.ilov.mcmods.tc;

import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import top.ilov.mcmods.tc.platform.Services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

@Data
public final class CakeConfig {

    private boolean enable_the_sound_of_eating_cakes = false;
    private boolean enable_tooltips_for_displaying_item = true;

    static File config = new File(Services.PLATFORM.getConfigDir().toFile(),
            "teleportcakes-common.json");

    @SneakyThrows
    public static CakeConfig loadConfig() {

        CakeConfig cakeConfig = new CakeConfig();

        if (!config.exists()) {
            write(cakeConfig);
        }

        BufferedReader reader;
        reader = Files.newBufferedReader(config.toPath());
        Gson gson = new Gson();
        cakeConfig = gson.fromJson(reader, CakeConfig.class);
        reader.close();

        return cakeConfig;

    }

    @SneakyThrows
    public static void write(CakeConfig cakeConfig) {

        FileWriter fileWriter;
        fileWriter = new FileWriter(config);
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
        fileWriter.write(gson.toJson(cakeConfig));
        fileWriter.close();

    }

}
