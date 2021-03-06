package test.ethereum.vm;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.ethereum.util.ByteUtil;
import org.ethereum.vm.Program;
import org.ethereum.vm.ProgramInvokeMockImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ProgramMemoryTest {

	ProgramInvokeMockImpl pi = null;
	Program program;
	ByteBuffer memory;
	
	@Before
	public void createProgram() {
		program = new Program(ByteUtil.EMPTY_BYTE_ARRAY, pi);
	}
	
	@Test
	public void testGetMemSize() {
		ByteBuffer memory = ByteBuffer.allocate(64);
		program.initMem(memory);
		assertEquals(64, program.getMemSize());
	}

	@Test
	@Ignore
	public void testMemorySave() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testMemoryLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testMemoryChunk1() {
        program.initMem(ByteBuffer.allocate(64));
		int offset = 128;
		int size = 32;
		program.memoryChunk(offset, size);
		assertEquals(160, program.getMemSize());
	}
	
	@Test // size 0 doesn't increate memory
	public void testMemoryChunk2() {
        program.initMem(ByteBuffer.allocate(64));
		int offset = 96;
		int size = 0;
		program.memoryChunk(offset, size);
		assertEquals(64, program.getMemSize());
	}

	@Test
	public void testAllocateMemory1() {

        program.initMem(ByteBuffer.allocate(64));
		int offset = 32;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory2() {

		// memory.limit() > offset, == size
		// memory.limit() < offset + size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 32;
		int size = 64;
		program.allocateMemory(offset, size);
		assertEquals(96, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory3() {

		// memory.limit() > offset, > size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 0;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory4() {

        program.initMem(ByteBuffer.allocate(64));
		int offset = 0;
		int size = 64;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory5() {

        program.initMem(ByteBuffer.allocate(64));
		int offset = 0;
		int size = 0;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory6() {

		// memory.limit() == offset, > size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 64;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(96, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory7() {

		// memory.limit() == offset - size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 96;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(128, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory8() {

        program.initMem(ByteBuffer.allocate(64));
		int offset = 0;
		int size = 96;
		program.allocateMemory(offset, size);
		assertEquals(96, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory9() {

		// memory.limit() < offset, > size
		// memory.limit() < offset - size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 96;
		int size = 0;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	/************************************************/
	
	
	@Test
	public void testAllocateMemory10() {

		// memory = null, offset > size
		int offset = 32;
		int size = 0;
		program.allocateMemory(offset, size);
		assertEquals(0, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory11() {

		// memory = null, offset < size
		int offset = 0;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(32, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory12() {

		// memory.limit() < offset, < size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 64;
		int size = 96;
		program.allocateMemory(offset, size);
		assertEquals(160, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory13() {

		// memory.limit() > offset, < size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 32;
		int size = 128;
		program.allocateMemory(offset, size);
		assertEquals(160, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemory14() {

		// memory.limit() < offset, == size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 96;
		int size = 64;
		program.allocateMemory(offset, size);
		assertEquals(160, program.getMemSize());
	}

	@Test
	public void testAllocateMemory15() {

		// memory.limit() == offset, < size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 64;
		int size = 96;
		program.allocateMemory(offset, size);
		assertEquals(160, program.getMemSize());
	}

	@Test
	public void testAllocateMemory16() {

		// memory.limit() == offset, == size
		// memory.limit() > offset - size
        program.initMem(ByteBuffer.allocate(64));
		int offset = 64;
		int size = 64;
		program.allocateMemory(offset, size);
		assertEquals(128, program.getMemSize());
	}

	@Test
	public void testAllocateMemory17() {

		// memory.limit() > offset + size
        program.initMem(ByteBuffer.allocate(96));
		int offset = 32;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(96, program.getMemSize());
	}

	@Test
	public void testAllocateMemoryUnrounded1() {

		// memory unrounded 
        program.initMem(ByteBuffer.allocate(64));
		int offset = 64;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(96, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemoryUnrounded2() {

		// offset unrounded 
        program.initMem(ByteBuffer.allocate(64));
		int offset = 16;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemoryUnrounded3() {

		// size unrounded 
        program.initMem(ByteBuffer.allocate(64));
		int offset = 64;
		int size = 16;
		program.allocateMemory(offset, size);
		assertEquals(96, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemoryUnrounded4() {

		// memory + offset unrounded 
        program.initMem(ByteBuffer.allocate(64));
		int offset = 16;
		int size = 32;
		program.allocateMemory(offset, size);
		assertEquals(64	, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemoryUnrounded5() {

		// memory + size unrounded 
        program.initMem(ByteBuffer.allocate(64));
		int offset = 32;
		int size = 16;
		program.allocateMemory(offset, size);
		assertEquals(64, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemoryUnrounded6() {

		// offset + size unrounded 
        program.initMem(ByteBuffer.allocate(32));
		int offset = 16;
		int size = 16;
		program.allocateMemory(offset, size);
		assertEquals(32, program.getMemSize());
	}
	
	@Test
	public void testAllocateMemoryUnrounded7() {

		// memory + offset + size unrounded 
        program.initMem(ByteBuffer.allocate(32));
		int offset = 16;
		int size = 16;
		program.allocateMemory(offset, size);
		assertEquals(32,program.getMemSize());
	}

    @Test
    public void testInitialInsert() {


        // todo: fix the array out of bound here
        int offset = 32;
        int size = 00;
        program.memorySave(32, 0, new byte[0]);
        assertEquals(32,program.getMemSize());
    }
}