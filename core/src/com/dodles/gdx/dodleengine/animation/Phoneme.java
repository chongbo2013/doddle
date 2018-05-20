package com.dodles.gdx.dodleengine.animation;

/**
 * Describes all available Phonemes and their Viseme mappings.
 */
public enum Phoneme {
    B(Viseme.M_B_P),
    M(Viseme.M_B_P),
    P(Viseme.M_B_P),
    SIL(Viseme.M_B_P),
    NSN(Viseme.M_B_P),
    
    CH(Viseme.T_S),
    D(Viseme.T_S),
    DH(Viseme.T_S),
    G(Viseme.T_S),
    J(Viseme.T_S),
    JH(Viseme.T_S),
    K(Viseme.T_S),
    S(Viseme.T_S),
    SH(Viseme.T_S),
    T(Viseme.T_S),
    TH(Viseme.T_S),
    Z(Viseme.T_S),
    ZH(Viseme.T_S),
    
    EH(Viseme.E),
    H(Viseme.E),
    IH(Viseme.E),
    IY(Viseme.E),
    Y(Viseme.E),
    
    N(Viseme.L_N),
    NG(Viseme.L_N),
    EL(Viseme.L_N),
    L(Viseme.L_N),
    
    R(Viseme.W_R),
    ER(Viseme.W_R),
    UW(Viseme.W_R),
    W(Viseme.W_R),
    AW(Viseme.W_R),
    
    AA(Viseme.A),
    AE(Viseme.A),
    AH(Viseme.A),
    AO(Viseme.A),
    AY(Viseme.A),
    EY(Viseme.A),
    HH(Viseme.A),
    
    OY(Viseme.O),
    OW(Viseme.O),
    
    UH(Viseme.U_Q),
    
    F(Viseme.F_V),
    V(Viseme.F_V);
    
    private Viseme viseme;
    
    Phoneme(Viseme viseme) {
        this.viseme = viseme;
    }
    
    /**
     * Returns the Viseme mapped to this Phoneme.
     */
    public Viseme getViseme() {
        return viseme;
    }
}
