package br.ufla.simulator.actors.events;

import java.awt.Color;

import br.ufla.simulator.simulation.Field;

/**
 * Evento natural que representa uma inundação em um determinado local do campo
 * de representação da simulação.
 * 
 * @author Guilherme Barbosa Ochikubo, Guilherme Henrique de Melo e Leonardo Henrique de Braz
 *
 */
public class Flood extends NaturalEvent {

	// tamanho máximo, em blocos, para cálculo da área do bloco de representação
	private static final int MAX_SIZE = 20;
	// duração máxima, em passos, que a inundação vai existir na simulação
	private static final int MAX_DURATION = 3;

	public Flood(Field field) {
		super(field);
	}

	@Override
	public int getMaxSize() {
		return MAX_SIZE;
	}

	@Override
	public int getMaxDuration() {
		return MAX_DURATION;
	}

	@Override
	public Color getColorRepresentation() {
		return Color.blue;
	}

}
