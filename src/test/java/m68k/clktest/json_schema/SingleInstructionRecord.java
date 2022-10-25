package m68k.clktest.json_schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Federico Berti
 * <p>
 * Copyright 2022
 */
public class SingleInstructionRecord {
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("initial")
        @Expose
        public Initial initial;
        @SerializedName("final")
        @Expose
        public Final _final;
        @SerializedName("length")
        @Expose
        public Long length;
        @SerializedName("transactions")
        @Expose
        public List<List<String>> transactions = null;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Initial getInitial() {
            return initial;
        }

        public void setInitial(Initial initial) {
            this.initial = initial;
        }

        public Final getFinal() {
            return _final;
        }

        public void setFinal(Final _final) {
            this._final = _final;
        }

        public Long getLength() {
            return length;
        }

        public void setLength(Long length) {
            this.length = length;
        }

        public List<List<String>> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<List<String>> transactions) {
            this.transactions = transactions;
        }

    public static class Final {

        @SerializedName("d0")
        @Expose
        public Long d0;
        @SerializedName("d1")
        @Expose
        public Long d1;
        @SerializedName("d2")
        @Expose
        public Long d2;
        @SerializedName("d3")
        @Expose
        public Long d3;
        @SerializedName("d4")
        @Expose
        public Long d4;
        @SerializedName("d5")
        @Expose
        public Long d5;
        @SerializedName("d6")
        @Expose
        public Long d6;
        @SerializedName("d7")
        @Expose
        public Long d7;
        @SerializedName("a0")
        @Expose
        public Long a0;
        @SerializedName("a1")
        @Expose
        public Long a1;
        @SerializedName("a2")
        @Expose
        public Long a2;
        @SerializedName("a3")
        @Expose
        public Long a3;
        @SerializedName("a4")
        @Expose
        public Long a4;
        @SerializedName("a5")
        @Expose
        public Long a5;
        @SerializedName("a6")
        @Expose
        public Long a6;
        @SerializedName("usp")
        @Expose
        public Long usp;
        @SerializedName("ssp")
        @Expose
        public Long ssp;
        @SerializedName("sr")
        @Expose
        public Long sr;
        @SerializedName("pc")
        @Expose
        public Long pc;
        @SerializedName("prefetch")
        @Expose
        public List<Long> prefetch = null;
        @SerializedName("ram")
        @Expose
        public List<List<Long>> ram = null;

        public Long getD0() {
            return d0;
        }

        public void setD0(Long d0) {
            this.d0 = d0;
        }

        public Long getD1() {
            return d1;
        }

        public void setD1(Long d1) {
            this.d1 = d1;
        }

        public Long getD2() {
            return d2;
        }

        public void setD2(Long d2) {
            this.d2 = d2;
        }

        public Long getD3() {
            return d3;
        }

        public void setD3(Long d3) {
            this.d3 = d3;
        }

        public Long getD4() {
            return d4;
        }

        public void setD4(Long d4) {
            this.d4 = d4;
        }

        public Long getD5() {
            return d5;
        }

        public void setD5(Long d5) {
            this.d5 = d5;
        }

        public Long getD6() {
            return d6;
        }

        public void setD6(Long d6) {
            this.d6 = d6;
        }

        public Long getD7() {
            return d7;
        }

        public void setD7(Long d7) {
            this.d7 = d7;
        }

        public Long getA0() {
            return a0;
        }

        public void setA0(Long a0) {
            this.a0 = a0;
        }

        public Long getA1() {
            return a1;
        }

        public void setA1(Long a1) {
            this.a1 = a1;
        }

        public Long getA2() {
            return a2;
        }

        public void setA2(Long a2) {
            this.a2 = a2;
        }

        public Long getA3() {
            return a3;
        }

        public void setA3(Long a3) {
            this.a3 = a3;
        }

        public Long getA4() {
            return a4;
        }

        public void setA4(Long a4) {
            this.a4 = a4;
        }

        public Long getA5() {
            return a5;
        }

        public void setA5(Long a5) {
            this.a5 = a5;
        }

        public Long getA6() {
            return a6;
        }

        public void setA6(Long a6) {
            this.a6 = a6;
        }

        public Long getUsp() {
            return usp;
        }

        public void setUsp(Long usp) {
            this.usp = usp;
        }

        public Long getSsp() {
            return ssp;
        }

        public void setSsp(Long ssp) {
            this.ssp = ssp;
        }

        public Long getSr() {
            return sr;
        }

        public void setSr(Long sr) {
            this.sr = sr;
        }

        public Long getPc() {
            return pc;
        }

        public void setPc(Long pc) {
            this.pc = pc;
        }

        public List<Long> getPrefetch() {
            return prefetch;
        }

        public void setPrefetch(List<Long> prefetch) {
            this.prefetch = prefetch;
        }

        public List<List<Long>> getRam() {
            return ram;
        }

        public void setRam(List<List<Long>> ram) {
            this.ram = ram;
        }
    }

    public static class Initial {

        @SerializedName("d0")
        @Expose
        public Long d0;
        @SerializedName("d1")
        @Expose
        public Long d1;
        @SerializedName("d2")
        @Expose
        public Long d2;
        @SerializedName("d3")
        @Expose
        public Long d3;
        @SerializedName("d4")
        @Expose
        public Long d4;
        @SerializedName("d5")
        @Expose
        public Long d5;
        @SerializedName("d6")
        @Expose
        public Long d6;
        @SerializedName("d7")
        @Expose
        public Long d7;
        @SerializedName("a0")
        @Expose
        public Long a0;
        @SerializedName("a1")
        @Expose
        public Long a1;
        @SerializedName("a2")
        @Expose
        public Long a2;
        @SerializedName("a3")
        @Expose
        public Long a3;
        @SerializedName("a4")
        @Expose
        public Long a4;
        @SerializedName("a5")
        @Expose
        public Long a5;
        @SerializedName("a6")
        @Expose
        public Long a6;
        @SerializedName("usp")
        @Expose
        public Long usp;
        @SerializedName("ssp")
        @Expose
        public Long ssp;
        @SerializedName("sr")
        @Expose
        public Long sr;
        @SerializedName("pc")
        @Expose
        public Long pc;
        @SerializedName("prefetch")
        @Expose
        public List<Long> prefetch = null;
        @SerializedName("ram")
        @Expose
        public List<List<Long>> ram = null;

        public Long getD0() {
            return d0;
        }

        public void setD0(Long d0) {
            this.d0 = d0;
        }

        public Long getD1() {
            return d1;
        }

        public void setD1(Long d1) {
            this.d1 = d1;
        }

        public Long getD2() {
            return d2;
        }

        public void setD2(Long d2) {
            this.d2 = d2;
        }

        public Long getD3() {
            return d3;
        }

        public void setD3(Long d3) {
            this.d3 = d3;
        }

        public Long getD4() {
            return d4;
        }

        public void setD4(Long d4) {
            this.d4 = d4;
        }

        public Long getD5() {
            return d5;
        }

        public void setD5(Long d5) {
            this.d5 = d5;
        }

        public Long getD6() {
            return d6;
        }

        public void setD6(Long d6) {
            this.d6 = d6;
        }

        public Long getD7() {
            return d7;
        }

        public void setD7(Long d7) {
            this.d7 = d7;
        }

        public Long getA0() {
            return a0;
        }

        public void setA0(Long a0) {
            this.a0 = a0;
        }

        public Long getA1() {
            return a1;
        }

        public void setA1(Long a1) {
            this.a1 = a1;
        }

        public Long getA2() {
            return a2;
        }

        public void setA2(Long a2) {
            this.a2 = a2;
        }

        public Long getA3() {
            return a3;
        }

        public void setA3(Long a3) {
            this.a3 = a3;
        }

        public Long getA4() {
            return a4;
        }

        public void setA4(Long a4) {
            this.a4 = a4;
        }

        public Long getA5() {
            return a5;
        }

        public void setA5(Long a5) {
            this.a5 = a5;
        }

        public Long getA6() {
            return a6;
        }

        public void setA6(Long a6) {
            this.a6 = a6;
        }

        public Long getUsp() {
            return usp;
        }

        public void setUsp(Long usp) {
            this.usp = usp;
        }

        public Long getSsp() {
            return ssp;
        }

        public void setSsp(Long ssp) {
            this.ssp = ssp;
        }

        public Long getSr() {
            return sr;
        }

        public void setSr(Long sr) {
            this.sr = sr;
        }

        public Long getPc() {
            return pc;
        }

        public void setPc(Long pc) {
            this.pc = pc;
        }

        public List<Long> getPrefetch() {
            return prefetch;
        }

        public void setPrefetch(List<Long> prefetch) {
            this.prefetch = prefetch;
        }

        public List<List<Long>> getRam() {
            return ram;
        }

        public void setRam(List<List<Long>> ram) {
            this.ram = ram;
        }

    }
}
