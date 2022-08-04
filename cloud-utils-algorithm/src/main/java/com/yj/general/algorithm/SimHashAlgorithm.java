package com.yj.general.algorithm;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName: SimhashAlgorithm
 * @Description: SimhashAlgorithm
 *  look up see https://blog.csdn.net/qq_32447301/article/details/104032712
 *
 * @Author: yanjianxun
 * @Date 2022/8/3
 * @Version 1.0
 */
public class SimHashAlgorithm {
    public static final int  HASH_SIZE          = 64;
    public static final long HASH_RANGE         = 2 ^ HASH_SIZE;
    public static MurmurHash hasher             = new MurmurHash();

    /**
     * use short cuts to obtains a speed optimized simhash calculation
     *
     * @param s
     *          input string
     * @return 64 bit simhash of input string
     */

    private static final int FIXED_CGRAM_LENGTH = 4;

    public static long computeOptimizedSimHashForString(String s) {
        return computeOptimizedSimHashForString(CharBuffer.wrap(s));
    }

    public static long computeOptimizedSimHashForString(CharBuffer s) {

        LongSet shingles = new LongOpenHashSet(Math.min(s.length(), 100000));

        int length = s.length();

        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < length - FIXED_CGRAM_LENGTH + 1; i++) {
            // extract an ngram

            long shingle = s.charAt(i);
            shingle <<= 16;
            shingle |= s.charAt(i + 1);
            shingle <<= 16;
            shingle |= s.charAt(i + 2);
            shingle <<= 16;
            shingle |= s.charAt(i + 3);

            shingles.add(shingle);
        }
        long timeEnd = System.currentTimeMillis();
        int v[] = new int[HASH_SIZE];
        byte longAsBytes[] = new byte[8];

        for (long shingle : shingles) {

            longAsBytes[0] = (byte) (shingle >> 56);
            longAsBytes[1] = (byte) (shingle >> 48);
            longAsBytes[2] = (byte) (shingle >> 40);
            longAsBytes[3] = (byte) (shingle >> 32);
            longAsBytes[4] = (byte) (shingle >> 24);
            longAsBytes[5] = (byte) (shingle >> 16);
            longAsBytes[6] = (byte) (shingle >> 8);
            longAsBytes[7] = (byte) (shingle);

            long longHash = FPGenerator.std64.fp(longAsBytes, 0, 8);
            for (int i = 0; i < HASH_SIZE; ++i) {
                boolean bitSet = ((longHash >> i) & 1L) == 1L;
                v[i] += (bitSet) ? 1 : -1;
            }
        }

        long simhash = 0;
        for (int i = 0; i < HASH_SIZE; ++i) {
            if (v[i] > 0) {
                simhash |= (1L << i);
            }
        }
        return simhash;
    }

    public static long computeSimHashFromString(Set<String> shingles) {

        int v[] = new int[HASH_SIZE];
        // compute a set of shingles
        for (String shingle : shingles) {
            byte[] bytes = shingle.getBytes();
            long longHash = FPGenerator.std64.fp(bytes, 0, bytes.length);
            // long hash1 = hasher.hash(bytes, bytes.length, 0);
            // long hash2 = hasher.hash(bytes, bytes.length, (int)hash1);
            // long longHash = (hash1 << 32) | hash2;
            for (int i = 0; i < HASH_SIZE; ++i) {
                boolean bitSet = ((longHash >> i) & 1L) == 1L;
                v[i] += (bitSet) ? 1 : -1;
            }
        }
        long simhash = 0;
        for (int i = 0; i < HASH_SIZE; ++i) {
            if (v[i] > 0) {
                simhash |= (1L << i);
            }
        }

        return simhash;
    }

    public static int hammingDistance(long hash1, long hash2) {
        long bits = hash1 ^ hash2;
        int count = 0;
        while (bits != 0) {
            bits &= bits - 1;
            ++count;
        }
        return count;
    }

    public static long rotate(long hashValue) {
        return (hashValue << 1) | (hashValue >>> -1);
    }

    public static void main(String[] args) {

        Set<String> strings = new HashSet<>();
        strings.add("你妈妈喊你回家吃饭哦，回家罗回家罗");
//        System.out.println(computeOptimizedSimHashForString(strings));
//        System.out.println(computeOptimizedSimHashForString("你妈妈喊你回家吃饭哦，回家罗回家罗"));

        String data1 = "二十大前夕，豫酒新3年振兴方案重磅出炉，“豫酒转型发展战略”持续深入推进，豫酒，再次启动了澎湃新动能，掀起了发展新浪潮！\n" +
                "\n" +
                "而作为豫酒振兴战略的参与者、践行者、推动者，杜康，又一次站到了聚光灯下!\n" +
                "\n" +
                "\n" +
                "\n" +
                "2022年4月，为加快推进杜康转型发展步伐， 在省委省政府的关怀下，洛阳市委市政府积极行动，洛阳市转型发展攻坚领导小组推进杜康转型发展工作专班办公室专项组织，严密统筹，科学引领，创新谋划了“豫酒振兴·杜康先行”洛阳市加快杜康转型发展推介会县区行系列活动，开展了全面的杜康品牌推介，营造出了浓厚的“洛阳人喝洛阳酒，洛阳人喝杜康酒”的市场氛围，为杜康的转型发展注入了强劲动能。\n" +
                "\n" +
                "\n" +
                "\n" +
                "3个月深入15县区，高站位、重效率、强落实\n" +
                "\n" +
                "“豫酒振兴·杜康先行”县区行系列活动，实行“走进杜康”与“深入县区”相结合，从开篇布局4月22日孟津首场落地，时至7月17日伊川收官之战，历时3个月由点及面，深入洛阳孟津区、偃师区、汝阳县、洛龙区、嵩县、栾川县、新安县、涧西区、西工区、瀍河区、老城区、伊滨区、宜阳县、洛宁县、伊川县15个县区，通过系列推介会，杜康在品牌提升、市场拓展和美誉度等方面实现了新突破。这得益于各级政府领导的高度重视、强力引领，洛阳各县区企事业单位的鼎力支持，也凸显出杜康抢抓机遇、乘势而上，复兴杜康的决心！\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "✦✦\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "图 | 企事业单位代表走进杜康\n" +
                "\n" +
                "\n" +
                "\n" +
                "洛阳市委副书记、市政府市长、党组书记徐衣显强烈关注，多次莅临杜康汝阳、伊川生产基地视察调研，参加县区行系列活动，并召开专题会议，强调要深入贯彻省委省政府豫酒振兴重大决策，发挥酒祖杜康文化优势、品牌优势、产业优势，推动杜康转型、提质升级，着力打造全省乃至全国具有重要影响力的知名品牌。\n" +
                "\n" +
                "洛阳市各级政府在加强领导组织，强化政策支持，优化发展环境，深化企业服务，净化经营环境，加大宣传力度等方面，给予了杜康全面支持，精准赋能杜康转型升级，助推全面复兴。\n" +
                "\n" +
                "\n" +
                "\n" +
                "全市联动、大商签约，洛阳人喝家乡酒氛围浓厚\n" +
                "\n" +
                "洛阳作为杜康的根据地，夯实市场至关重要，洛阳市场稳扎稳打、气势如虹，才能让杜康“走出去”更有底气。此次系列推介会，洛阳市政府挂帅，全市上下联动，为杜康精准搭台、“牵线搭桥”，推动了市场发展。至系列活动结束，邀请了数千位企事业单位领导、各界大商走进杜康，并与数百家重点企事业及商会协会签订了战略合作协议，涉及合作金额数千万元。\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "签约\n" +
                "\n" +
                "而在县区行系列活动的强劲势能下，众多经销商与亮相两月有余的杜康战略新品“杜康造酒”签订了经销协议，杜康高端产品小封坛填补了若干空白市场，优化了市场布局。杜康市场布局进程蹄疾步稳，提升了在洛阳本地的市场占有率。\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "县区行系列活动不仅提振了经销商和广大客户的信心，在消费者层面，品牌美誉度也极大增强。加之杜康通过实行抢红包、赢代驾等销售惠民政策回馈消费者，并连续开展杜康美酒节，注重杜康酒“口感培育”……与消费者深度互动，提升消费者认知和偏好，让美誉度切切实实得到提升，让“洛阳人喝洛阳酒，洛阳人喝杜康酒”的市场氛围逐渐高涨。\n" +
                "\n" +
                "\n" +
                "\n" +
                "练好内功、深化发展，政企合力助推复兴\n" +
                "\n" +
                "在“豫酒振兴·杜康先行”县区行伊川站活动总结会议上，市工信局、商务局、市场监管局、汝阳县政府、伊川县政府依次汇报了支持杜康转型发展工作开展情况、存在问题及下步工作打算。\n" +
                "\n" +
                "杜康控股领导在总结会议上强调“加强组织领导，形成工作合力；采取‘加大资金投入，重点项目有新推进；系列新品上市，产品结构有新优化；加强软实力输出，杜康文化有新宣传；强化对外宣传，产品形象有新提升；立足中原市场，省内外市场有新拓展；积极拥抱互联网，产品数字化有新突破’多种措施，推进转型发展取得的成果；积极开展推介活动，消费氛围开始形成。”\n" +
                "\n" +
                "\n" +
                "\n" +
                "在认真听取汇报后，徐市长对杜康控股积极投入取得的显著成效表示赞许，并提出五点要求：一、项目为王，扩能提质；二、完善体系，做响品牌；三、精准施策，做好现代营销；四、市场导向，激发人才活力，健全人才机制；五，强化服务，优化营商环境，并再次强调，“必须把杜康的发展当做全市的一项大事来抓”，全面推动杜康复兴！\n" +
                "\n" +
                "\n" +
                "\n" +
                "喜迎二十大，奋进新征程！\n" +
                "\n" +
                "作为周总理“复兴杜康 为国争光”殷殷嘱托的千年品牌，作为在党和国家关怀下一路成长的民族企业，作为省市县各级政府重点扶持的一张河南名片，作为深深根植于洛阳的本土企业，杜康将趁着豫酒振兴东风，勇担使命，不负洛阳人民期盼，不负河南父老乡亲，讲好杜康故事，弘扬传统文化，为“建强副中心、形成增长极、重振洛阳辉煌”作出更大贡献，为豫酒全面转型发展贡献杜康力量，助力中原更出彩，匠心献礼二十大！\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "来源：中酒圈";

        String data2 = "二十大前夕，豫酒新3年振兴方案重磅出炉，“豫酒转型发展战略”持续深入推进，豫酒，再次启动了澎湃新动能，掀起了发展新浪潮！\n" +
                "\n" +
                "而作为豫酒振兴战略的参与者、践行者、推动者，杜康，又一次站到了聚光灯下!\n" +
                "\n" +
                "\n" +
                "\n" +
                "2022年4月，为加快推进杜康转型发展步伐， 在省委省政府的关怀下，洛阳市委市政府积极行动，洛阳市转型发展攻坚领导小组推进杜康转型发展工作专班办公室专项组织，严密统筹，科学引领，创新谋划了“豫酒振兴·杜康先行”洛阳市加快杜康转型发展推介会县区行系列活动，开展了全面的杜康品牌推介，营造出了浓厚的“洛阳人喝洛阳酒，洛阳人喝杜康酒”的市场氛围，为杜康的转型发展注入了强劲动能。\n" +
                "\n" +
                "\n" +
                "\n" +
                "3个月深入15县区，高站位、重效率、强落实\n" +
                "\n" +
                "“豫酒振兴·杜康先行”县区行系列活动，实行“走进杜康”与“深入县区”相结合，从开篇布局4月22日孟津首场落地，时至7月17日伊川收官之战，历时3个月由点及面，深入洛阳孟津区、偃师区、汝阳县、洛龙区、嵩县、栾川县、新安县、涧西区、西工区、瀍河区、老城区、伊滨区、宜阳县、洛宁县、伊川县15个县区，通过系列推介会，杜康在品牌提升、市场拓展和美誉度等方面实现了新突破。这得益于各级政府领导的高度重视、强力引领，洛阳各县区企事业单位的鼎力支持，也凸显出杜康抢抓机遇、乘势而上，复兴杜康的决心！\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "✦✦\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "图 | 企事业单位代表走进杜康\n" +
                "\n" +
                "\n" +
                "\n" +
                "洛阳市委副书记、市政府市长、党组书记徐衣显强烈关注，多次莅临杜康汝阳、伊川生产基地视察调研，参加县区行系列活动，并召开专题会议，强调要深入贯彻省委省政府豫酒振兴重大决策，发挥酒祖杜康文化优势、品牌优势、产业优势，推动杜康转型、提质升级，着力打造全省乃至全国具有重要影响力的知名品牌。\n" +
                "\n" +
                "洛阳市各级政府在加强领导组织，强化政策支持，优化发展环境，深化企业服务，净化经营环境，加大宣传力度等方面，给予了杜康全面支持，精准赋能杜康转型升级，助推全面复兴。\n" +
                "\n" +
                "\n" +
                "\n" +
                "全市联动、大商签约，洛阳人喝家乡酒氛围浓厚\n" +
                "\n" +
                "洛阳作为杜康的根据地，夯实市场至关重要，洛阳市场稳扎稳打、气势如虹，才能让杜康“走出去”更有底气。此次系列推介会，洛阳市政府挂帅，全市上下联动，为杜康精准搭台、“牵线搭桥”，推动了市场发展。至系列活动结束，邀请了数千位企事业单位领导、各界大商走进杜康，并与数百家重点企事业及商会协会签订了战略合作协议，涉及合作金额数千万元。\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "签约\n" +
                "\n" +
                "而在县区行系列活动的强劲势能下，众多经销商与亮相两月有余的杜康战略新品“杜康造酒”签订了经销协议，杜康高端产品小封坛填补了若干空白市场，优化了市场布局。杜康市场布局进程蹄疾步稳，提升了在洛阳本地的市场占有率。\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "县区行系列活动不仅提振了经销商和广大客户的信心，在消费者层面，品牌美誉度也极大增强。加之杜康通过实行抢红包、赢代驾等销售惠民政策回馈消费者，并连续开展杜康美酒节，注重杜康酒“口感培育”……与消费者深度互动，提升消费者认知和偏好，让美誉度切切实实得到提升，让“洛阳人喝洛阳酒，洛阳人喝杜康酒”的市场氛围逐渐高涨。\n" +
                "\n" +
                "\n" +
                "\n" +
                "练好内功、深化发展，政企合力助推复兴\n" +
                "\n" +
                "在“豫酒振兴·杜康先行”县区行伊川站活动总结会议上，市工信局、商务局、市场监管局、汝阳县政府、伊川县政府依次汇报了支持杜康转型发展工作开展情况、存在问题及下步工作打算。\n" +
                "\n" +
                "杜康控股领导在总结会议上强调“加强组织领导，形成工作合力；采取‘加大资金投入，重点项目有新推进；系列新品上市，产品结构有新优化；加强软实力输出，杜康文化有新宣传；强化对外宣传，产品形象有新提升；立足中原市场，省内外市场有新拓展；积极拥抱互联网，产品数字化有新突破’多种措施，推进转型发展取得的成果；积极开展推介活动，消费氛围开始形成。”\n" +
                "\n" +
                "\n" +
                "\n" +
                "在认真听取汇报后，徐市长对杜康控股积极投入取得的显著成效表示赞许，并提出五点要求：一、项目为王，扩能提质；二、完善体系，做响品牌；三、精准施策，做好现代营销；四、市场导向，激发人才活力，健全人才机制；五，强化服务，优化营商环境，并再次强调，“必须把杜康的发展当做全市的一项大事来抓”，全面推动杜康复兴！\n" +
                "\n" +
                "\n" +
                "\n" +
                "喜迎二十大，奋进新征程！\n" +
                "\n" +
                "作为周总理“复兴杜康 为国争光”殷殷嘱托的千年品牌，作为在党和国家关怀下一路成长的民族企业，作为省市县各级政府重点扶持的一张河南名片，作为深深根植于洛阳的本土企业，杜康将趁着豫酒振兴东风，勇担使命，不负洛阳人民期盼，不负河南父老乡亲，讲好杜康故事，弘扬传统文化，为“建强副中心、形成增长极、重振洛阳辉煌”作出更大贡献，为豫酒全面转型发展贡献杜康力量，助力中原更出彩，匠心献礼二十大！同时祝贺泰山景区成立30周年！\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "来源：中酒圈";


        System.out.println(hammingDistance(computeOptimizedSimHashForString(data1), computeOptimizedSimHashForString(data2)));

//        try {
//            // File file1 = new File("/Users/rana/academia.edu_01.html");
//            // File file2 = new File("/Users/rana/academia.edu_02.html");
//
//            File file1 = new File(args[0]);
//            File file2 = new File(args[1]);
//
//            byte data1[] = new byte[(int) file1.length()];
//            byte data2[] = new byte[(int) file2.length()];
//            FileInputStream stream1 = new FileInputStream(file1);
//            FileInputStream stream2 = new FileInputStream(file2);
//            stream1.read(data1);
//            stream2.read(data2);
//            String string1 = new String(data1);
//            String string2 = new String(data2);
//
//            long timeStart = System.currentTimeMillis();
//            long simhash1 = computeSimHashFromString(Shingle.shingles(string1));
//            long timeEnd = System.currentTimeMillis();
//            System.out.println("Old Calc for Document A Took:"
//                    + (timeEnd - timeStart));
//            timeStart = System.currentTimeMillis();
//            long simhash2 = computeSimHashFromString(Shingle.shingles(string2));
//            timeEnd = System.currentTimeMillis();
//            System.out.println("Old Calc for Document B Took:"
//                    + (timeEnd - timeStart));
//            timeStart = System.currentTimeMillis();
//            long simhash3 = computeOptimizedSimHashForString(string1);
//            timeEnd = System.currentTimeMillis();
//            System.out.println("New Calc for Document A Took:"
//                    + (timeEnd - timeStart));
//            timeStart = System.currentTimeMillis();
//            long simhash4 = computeOptimizedSimHashForString(string2);
//            timeEnd = System.currentTimeMillis();
//            System.out.println("New Calc for Document B Took:"
//                    + (timeEnd - timeStart));
//
//            int hammingDistance = hammingDistance(simhash1, simhash2);
//            int hammingDistance2 = hammingDistance(simhash3, simhash4);
//
//            System.out.println("hammingdistance Doc (A) to Doc(B) OldWay:"
//                    + hammingDistance);
//            System.out.println("hammingdistance Doc (A) to Doc(B) NewWay:"
//                    + hammingDistance2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
