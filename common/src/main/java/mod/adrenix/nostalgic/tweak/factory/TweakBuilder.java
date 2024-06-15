package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.TweakIssue;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class TweakBuilder<B extends TweakBuilder<B>>
{
    /* Fields */

    final TweakEnv env;
    final Container container;
    final Set<TweakIssue> modIssues = new HashSet<>();
    final Set<ModTracker> conflictMods = new HashSet<>();
    BooleanSupplier andIf = BooleanSupplier.ALWAYS;
    BooleanSupplier ignoreIf = BooleanSupplier.NEVER;
    TweakAlert alert = TweakAlert.NONE;
    TweakStatus status = TweakStatus.WAIT;

    boolean newForUpdate = false;
    boolean noNetworkCheck = false;
    boolean hasWarningTag = false;
    boolean doesChunkReload = false;
    boolean doesResourceReload = false;
    boolean noSSO = false;
    boolean top = false;

    /* Constructor */

    TweakBuilder(TweakEnv env, Container container)
    {
        this.env = env;
        this.container = container;

        if (NostalgicTweaks.isServer())
            this.status = TweakStatus.FAIL;
    }

    /* Abstraction */

    /**
     * For builders to make use of proper functional chaining, a pointer to the extending builder instance is required
     * so that this abstract tweak builder can expose its methods properly. Below is a simple example of what an
     * override self-method would look like.
     *
     * <pre>
     * &#64;Override
     * public Builder self()
     * {
     *     return this;
     * }
     * </pre>
     * <p>
     * Where {@code Builder} is replaced with the class name that extended this tweak builder.
     *
     * @return A pointer to the implementing builder instance.
     */
    abstract B self();

    /* Methods */

    /**
     * Reference {@code see also}.
     *
     * @see Tweak#isNew()
     */
    public B newForUpdate()
    {
        this.newForUpdate = true;
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @see Tweak#isTop()
     */
    public B top()
    {
        this.top = true;
        return this.self();
    }

    /**
     * Set this tweak's status to {@code LOADED}. Reference {@code see also}.
     *
     * @see Tweak#getStatus()
     */
    public B load()
    {
        this.status = TweakStatus.LOADED;
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @see Tweak#isIgnored()
     */
    public B ignore()
    {
        this.ignoreIf = BooleanSupplier.ALWAYS;
        return this.self();
    }

    /**
     * A supplier that indicates if a tweak should be ignored. This is useful if a tweak should only be ignored in
     * certain circumstances. Such as a mod loader specific tweak.
     *
     * @param condition A {@link BooleanSupplier} that provides whether a tweak should be considered ignored.
     */
    public B ignoreIf(BooleanSupplier condition)
    {
        this.ignoreIf = condition;
        return this.self();
    }

    /**
     * A supplier that indicates if a tweak should be considered enabled. This is useful if an additional check should
     * be made in the mod config when it gets the value of a tweak. If the supplier returns a {@code false} boolean
     * value then the tweak will give back its disabled value.
     *
     * @param condition A {@link BooleanSupplier} that provides whether a tweak should be considered enabled.
     */
    public B andIf(BooleanSupplier condition)
    {
        this.andIf = condition;
        return this.self();
    }

    /**
     * The chunks will be reloaded when this tweak's state is changed. Reference {@code see also}.
     *
     * @see Tweak#isChunkReloadRequired()
     */
    public B reloadChunks()
    {
        this.doesChunkReload = true;
        return this.self();
    }

    /**
     * The game's resources will be reloaded when this tweak's state is changed. Reference {@code see also}.
     *
     * @see Tweak#isResourceReloadRequired()
     */
    public B reloadResources()
    {
        this.doesResourceReload = true;
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @see Tweak#isNetworkCheckIgnored()
     */
    public B ignoreNetworkCheck()
    {
        this.noNetworkCheck = true;
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @param mods A varargs list of {@link ModTracker} enumerations that conflict with this tweak.
     * @see Tweak#isModConflict()
     */
    public B conflictMods(ModTracker... mods)
    {
        this.conflictMods.addAll(Arrays.asList(mods));
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @param issues A varargs list of {@link TweakIssue} enumerations that are known for this tweak.
     * @see Tweak#getModIssues()
     */
    public B modIssues(TweakIssue... issues)
    {
        this.modIssues.addAll(Arrays.asList(issues));
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @see Tweak#isWarningTag()
     */
    public B warningTag()
    {
        this.hasWarningTag = true;
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @see Tweak#isNotSSO()
     */
    public B noSSO()
    {
        this.noSSO = true;
        return this.self();
    }

    /**
     * Reference {@code see also}.
     *
     * @param alert The {@link TweakAlert} enumeration that is associated with this tweak.
     * @see Tweak#isAlertTag()
     */
    public B alert(TweakAlert alert)
    {
        this.alert = alert;
        return this.self();
    }
}
