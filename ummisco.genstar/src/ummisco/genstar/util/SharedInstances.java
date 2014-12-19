package ummisco.genstar.util;

import java.security.SecureRandom;
import java.util.Random;

import org.uncommons.maths.random.SeedGenerator;

public class SharedInstances {

	public static final Random RandomNumberGenerator = new Random();
	
//	public static final Random RandomNumberGenerator = new MersenneTwisterRNG(new MySeedGenerator());
	
	static class MySeedGenerator implements SeedGenerator {

		/** The seed. */
		private Long seed = null;
		private final SecureRandom SEED_SOURCE = new SecureRandom();
		
		
		MySeedGenerator() {}

		@Override
		public byte[] generateSeed(int length) {
			if ( seed == null ) { return SEED_SOURCE.generateSeed(length); }
			return createSeed(seed, length);
		}
		
		private byte[] createSeed(final Long seed, final int length) {
			long l = seed;
			final byte[] result = new byte[length];
			switch (length) {
				case 4:
					for ( int i1 = 0; i1 < 4; i1++ ) {
						result[i1] = (byte) (l & 0xff);
						l >>= 8;
					}
					break;
				case 8:
					for ( int i = 0; i < 8; i++ ) {
						result[i] = (byte) l;
						l >>= 8;
					}
					break;
				case 16:
					for ( int i = 0; i < 8; i++ ) {
						result[i] = result[i + 8] = (byte) (l & 0xff);
						l >>= 8;
					}
			}
			return result;
		}
	}
}
