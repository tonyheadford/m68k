package m68k.cpu.operand;

import m68k.cpu.Cpu;

/**
 * AbstractOperand
 *
 * @author Federico Berti
 */
public abstract class AbstractOperand implements Operand {
	protected final int index;
	protected final Cpu cpu;

	protected AbstractOperand(Cpu cpu){
		this.index = OperandTiming.operandIndexMap.getOrDefault(getClass(), -1);
		this.cpu = cpu;
	}
	@Override
	public final int index() {
		return index;
	}
}
