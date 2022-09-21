package materialtweaker.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;

@Mod(modid = MaterialTweaker.MODID, version = MaterialTweaker.VERSION, name = MaterialTweaker.NAME)
public class MaterialTweaker
{
    public static final String MODID = "materialtweaker";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "MaterialTweaker";
	
	@Instance(MODID)
	public static MaterialTweaker instance;
}
