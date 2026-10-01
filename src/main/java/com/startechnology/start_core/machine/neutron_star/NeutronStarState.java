package com.startechnology.start_core.machine.neutron_star;

import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.F_DEATH;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.F_MAX;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.GLITCH_SIZE;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.J_ACC;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.M_TOV;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.braking;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarBalance.glitchChance;

public final class NeutronStarState {

    public NeutronStarPhase phase = NeutronStarPhase.IDLE;
    public long phaseTicks;
    public double mass;
    public double spin;
    public int glitchCount;
    public double braking;
    public double draw;
    public double spinUp;
    public double massRate;
    private double accretedSpin;
    private double accretedMass;

    public void capture(double capturedMass, double capturedSpin) {
        reset();
        mass = capturedMass;
        spin = capturedSpin;
        enter(NeutronStarPhase.CAPTURING);
    }

    public void tick() {
        phaseTicks++;
        switch (phase) {
            case CAPTURING -> {
                if (phaseTicks >= phase.duration) enter(NeutronStarPhase.ACTIVE);
            }
            case FADING, COLLAPSING, DISRUPTING -> {
                if (phaseTicks >= phase.duration) reset();
            }
            default -> {}
        }
    }

    public double spinRequest(double targetSpin, double draw, double dt, int speed) {
        if (phase != NeutronStarPhase.ACTIVE || spin >= targetSpin) return 0;
        return targetSpin - spin + (braking(spin) + draw) * speed * dt;
    }

    public double spinPerMb(double fuelSpinPerMb) {
        return mass > 0 ? J_ACC * fuelSpinPerMb / mass : 0;
    }

    public void accrete(double mb, double massPerMb, double fuelSpinPerMb) {
        accretedSpin += mb * spinPerMb(fuelSpinPerMb);
        accretedMass += mb * massPerMb;
    }

    public void step(double dt, double draw, int speed) {
        if (phase != NeutronStarPhase.ACTIVE) {
            clearRates();
            return;
        }
        braking = braking(spin) * speed;
        this.draw = draw * speed;
        spinUp = accretedSpin / dt;
        massRate = accretedMass / dt;
        spin = Math.min(Math.max(spin + accretedSpin - (braking + this.draw) * dt, 0), F_MAX);
        mass += accretedMass;
        accretedSpin = 0;
        accretedMass = 0;

        if (mass >= M_TOV) {
            enter(NeutronStarPhase.COLLAPSING);
        } else if (spin < F_DEATH) {
            enter(NeutronStarPhase.FADING);
        }
    }

    public boolean glitch(double roll) {
        if (phase != NeutronStarPhase.ACTIVE || roll >= glitchChance(spin)) return false;
        spin = Math.min(spin * (1 + GLITCH_SIZE), F_MAX);
        glitchCount++;
        return true;
    }

    public void disrupt() {
        if (!phase.hasStar() || phase.isEnding()) return;
        enter(NeutronStarPhase.DISRUPTING);
    }

    public void reset() {
        phase = NeutronStarPhase.IDLE;
        phaseTicks = 0;
        mass = 0;
        spin = 0;
        glitchCount = 0;
        clearRates();
    }

    private void clearRates() {
        braking = 0;
        draw = 0;
        spinUp = 0;
        massRate = 0;
        accretedSpin = 0;
        accretedMass = 0;
    }

    private void enter(NeutronStarPhase next) {
        phase = next;
        phaseTicks = 0;
        if (next != NeutronStarPhase.ACTIVE) clearRates();
    }
}
